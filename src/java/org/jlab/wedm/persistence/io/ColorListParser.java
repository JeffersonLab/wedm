package org.jlab.wedm.persistence.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Logger;
import org.jlab.wedm.persistence.model.AlarmColors;
import org.jlab.wedm.persistence.model.ColorPalette;
import org.jlab.wedm.persistence.model.EDLColor;
import org.jlab.wedm.persistence.model.EDLColorConstant;
import org.jlab.wedm.persistence.model.EDLColorRule;

/**
 *
 * @author ryans
 */
public class ColorListParser extends EDLParser {

    private static final Logger LOGGER = Logger.getLogger(ColorListParser.class.getName());

    public ColorPalette parse(String filename) throws FileNotFoundException {
        Map<Integer, EDLColor> indexMap = new HashMap<>();
        Map<String, EDLColor> nameMap = new HashMap<>();
        AlarmColors alarmColors = new AlarmColors();
        List<EDLColorConstant> staticColors = new ArrayList<>();
        List<EDLColorRule> ruleColors = new ArrayList<>();
        int maxColors = 256;

        File clist = new File(EDL_ROOT_DIR + File.separator + filename);

        try (Scanner scanner = new Scanner(clist)) {

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                if (line.startsWith("max")) {
                    //LOGGER.log(Level.FINEST, "Setting max colors");
                    String[] tokens = line.split("=");
                    //LOGGER.log(Level.FINEST, "Max: {0}", tokens[1]);
                    maxColors = Integer.decode(tokens[1]);
                }

                String[] tokens = line.split("\\s+");
                if (tokens.length > 0) {
                    switch (tokens[0]) {
                        case "static":
                            //LOGGER.log(Level.FINEST, "Found: static");
                            Integer index = Integer.parseInt(tokens[1]);
                            String colorname = stripQuotes(tokens[2]);
                            Integer r = Integer.parseInt(tokens[4]);
                            Integer g = Integer.parseInt(tokens[5]);
                            Integer b = Integer.parseInt(tokens[6]);

                            if (maxColors != 256) {
                                r = downsampleRgb65kTo256(r);
                                g = downsampleRgb65kTo256(g);
                                b = downsampleRgb65kTo256(b);
                            }

                            EDLColor color = new EDLColorConstant(index, colorname, r, g, b);
                            indexMap.put(index, color);
                            nameMap.put(colorname, color);
                            staticColors.add((EDLColorConstant) color);
                            break;
                        case "alarm":
                            line = scanner.nextLine();
                            String[] pieces = line.split(":");
                            String disconnected = stripQuotes(pieces[1]);
                            EDLColor c = nameMap.get(disconnected);
                            alarmColors.disconnectedAlarm = (EDLColorConstant) c;
                            line = scanner.nextLine();
                            pieces = line.split(":");
                            String invalid = stripQuotes(pieces[1]);
                            c = nameMap.get(invalid);
                            alarmColors.invalidAlarm = (EDLColorConstant) c;
                            line = scanner.nextLine();
                            pieces = line.split(":");
                            String minor = stripQuotes(pieces[1]);
                            c = nameMap.get(minor);
                            alarmColors.minorAlarm = (EDLColorConstant) c;
                            line = scanner.nextLine();
                            pieces = line.split(":");
                            String major = stripQuotes(pieces[1]);
                            c = nameMap.get(major);
                            alarmColors.majorAlarm = (EDLColorConstant) c;
                            line = scanner.nextLine();
                            pieces = line.split(":");
                            String noalarm = stripQuotes(pieces[1]);
                            c = nameMap.get(noalarm);
                            alarmColors.noAlarm = (EDLColorConstant) c;
                            break;
                        case "rule":
                            index = Integer.parseInt(tokens[1]);
                            colorname = stripQuotes(tokens[2]);
                            String firstColor;

                            // We convert EDL Color rules into JavaScript case statements with A = input and B = output
                            String expression = "switch(true) {";

                            String value = scanner.nextLine();
                            value = value.trim();
                            String[] parts = value.split(":");
                            String condition = stripQuotes(parts[0]);
                            if ("default".equals(condition)) {
                                condition = condition + ": ";
                            } else {
                                condition = "case (A " + condition + "): ";
                                condition = condition.replace("&&", "&& A");
                                condition = condition.replace("||", "|| A");
                                // Must wait until we have varibles in place to do following as "=" replace must be preceded by something
                                condition = convertEDMExpressionToJavaScript(condition);                                
                            }
                            String colorValue = stripQuotes(parts[1]);
                            expression = expression + condition + "B = '" + colorValue
                                    + "'; break;";

                            firstColor = colorValue;

                            while (true) {
                                value = scanner.nextLine();

                                value = value.trim();

                                if ("}".equals(value)) {
                                    break;
                                }

                                if ("".equals(value)) {
                                    continue;
                                }

                                //System.out.println("line: " + value);
                                parts = value.split(":");
                                condition = stripQuotes(parts[0]);
                                if ("default".equals(condition)) {
                                    condition = condition + ": ";
                                } else {
                                    condition = "case (A " + condition + "): ";
                                    condition = condition.replace("&&", "&& A");
                                    condition = condition.replace("||", "|| A");
                                    condition = convertEDMExpressionToJavaScript(condition);                                    
                                }
                                colorValue = stripQuotes(parts[1]);
                                expression = expression + condition + "B = '" + colorValue
                                        + "'; break;";
                            }

                            expression = expression + "}";

                            //System.out.println("expression: " + expression);
                            color = new EDLColorRule(index, colorname, expression, firstColor);
                            indexMap.put(index, color);
                            nameMap.put(colorname, color);
                            ruleColors.add((EDLColorRule) color);
                            break;
                        default:
                            //LOGGER.log(Level.FINEST, "Ignoring Line: {0}", line);
                            break;
                    }
                }
            }
        }
        return new ColorPalette(indexMap, nameMap, maxColors, staticColors, alarmColors, ruleColors);
    }

    private String convertEDMExpressionToJavaScript(String expr) {
        //System.out.println("before: " + expr);
        
        expr = expr.replaceAll("([^<>\\!])=", "$1=="); // Match =, but not >= or <= or !=      
        
        expr = expr.replaceAll("#", "!=");
        expr = expr.replaceAll("and", "&");
        expr = expr.replaceAll("or", "|");
        expr = expr.replaceAll("abs", "Math.abs");
        expr = expr.replaceAll("min", "Math.min");
        expr = expr.replaceAll("max", "Math.max");

        //System.out.println("after: " + expr);        
        
        return expr;
    }
}
