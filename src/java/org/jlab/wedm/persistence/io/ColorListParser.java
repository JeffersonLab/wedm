package org.jlab.wedm.persistence.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Logger;
import org.jlab.wedm.persistence.model.AlarmColors;
import org.jlab.wedm.persistence.model.ColorList;
import org.jlab.wedm.persistence.model.EDLColor;

/**
 *
 * @author ryans
 */
public class ColorListParser extends EDMParser {

    private static final Logger LOGGER = Logger.getLogger(ColorListParser.class.getName());

    public ColorList parse(String filename) throws FileNotFoundException {
        Map<Integer, EDLColor> indexMap = new HashMap<>();
        Map<String, EDLColor> nameMap = new HashMap<>();
        AlarmColors alarmColors = new AlarmColors();
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

                            EDLColor color = new EDLColor(colorname, r, g, b);
                            indexMap.put(index, color);
                            nameMap.put(colorname, color);
                            break;
                        case "alarm":
                            line = scanner.nextLine();
                            String[] pieces = line.split(":");
                            String disconnected = stripQuotes(pieces[1]);
                            EDLColor c = nameMap.get(disconnected);
                            alarmColors.disconnectedAlarm = c;
                            line = scanner.nextLine();
                            pieces = line.split(":");
                            String invalid = stripQuotes(pieces[1]);
                            c = nameMap.get(invalid);
                            alarmColors.invalidAlarm = c;
                            line = scanner.nextLine();
                            pieces = line.split(":");
                            String minor = stripQuotes(pieces[1]);
                            c = nameMap.get(minor);
                            alarmColors.minorAlarm = c;
                            line = scanner.nextLine();
                            pieces = line.split(":");
                            String major = stripQuotes(pieces[1]);
                            c = nameMap.get(major);
                            alarmColors.majorAlarm = c;
                            line = scanner.nextLine();
                            pieces = line.split(":");
                            String noalarm = stripQuotes(pieces[1]);
                            c = nameMap.get(noalarm);
                            alarmColors.noAlarm = c;
                            break;
                        default:
                            //LOGGER.log(Level.FINEST, "Ignoring Line: {0}", line);
                            break;
                    }
                }
            }
        }
        return new ColorList(indexMap, nameMap, maxColors, alarmColors);
    }
}
