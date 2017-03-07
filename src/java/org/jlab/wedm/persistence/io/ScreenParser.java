package org.jlab.wedm.persistence.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jlab.wedm.persistence.model.ActiveDynamicSymbol;
import org.jlab.wedm.persistence.model.ActiveGroup;
import org.jlab.wedm.persistence.model.ActiveSymbol;
import org.jlab.wedm.persistence.model.html.ActiveChoiceButton;
import org.jlab.wedm.persistence.model.svg.ActiveLine;
import org.jlab.wedm.persistence.model.html.ActiveButton;
import org.jlab.wedm.persistence.model.html.ActiveXText;
import org.jlab.wedm.persistence.model.html.ActiveXTextDsp;
import org.jlab.wedm.persistence.model.ColorList;
import org.jlab.wedm.persistence.model.EDLColor;
import org.jlab.wedm.persistence.model.EDLFont;
import org.jlab.wedm.persistence.model.html.HtmlScreenObject;
import org.jlab.wedm.persistence.model.html.RelatedDisplay;
import org.jlab.wedm.persistence.model.Screen;
import org.jlab.wedm.persistence.model.ScreenObject;
import org.jlab.wedm.persistence.model.ScreenProperties;
import org.jlab.wedm.persistence.model.html.ActiveMessageButton;
import org.jlab.wedm.persistence.model.html.ShellCommand;
import org.jlab.wedm.persistence.model.html.TextScreenObject;
import org.jlab.wedm.persistence.model.svg.ActiveArc;
import org.jlab.wedm.persistence.model.svg.ActiveBarMonitor;
import org.jlab.wedm.persistence.model.svg.ActiveByte;
import org.jlab.wedm.persistence.model.svg.ActiveCircle;
import org.jlab.wedm.persistence.model.svg.ActiveRectangle;

public class ScreenParser extends EDMParser {

    private static final Logger LOGGER = Logger.getLogger(ScreenParser.class.getName());

    public Screen parse(String name, ColorList colorList, boolean isSymbolFile) throws
            FileNotFoundException {

        if (name == null) {
            return null; // TODO: should we return a screen that says no file name given?
        }

        if (!name.endsWith(".edl")) {
            name = name + ".edl";
        }

        File edl = new File(name);

        if (!edl.isAbsolute()) {
            edl = new File(EDL_ROOT_DIR + File.separator + name);
        }

        int objectId = 0;

        ScreenProperties properties = new ScreenProperties();
        List<ScreenObject> screenObjects = new ArrayList<>();
        List<ActiveSymbol> symbolObjects = new ArrayList<>();

        try (Scanner scanner = new Scanner(edl)) {

            ScreenObject last = null;
            Deque<ActiveGroup> groupStack = new ArrayDeque<>();

            while (scanner.hasNextLine()) {
                boolean added = false;
                String line = scanner.nextLine();
                String[] tokens = line.split("\\s+");
                if (tokens.length > 0) {
                    try {
                        switch (tokens[0]) {
                            case "beginScreenProperties":
                                //LOGGER.log(Level.FINEST, "Found: beginScreenProperties");
                                last = properties;
                                break;
                            case "object":
                                //LOGGER.log(Level.FINEST, "Found: object");

                                ScreenObject obj;

                                switch (tokens[1]) {
                                    case "activeXTextClass":
                                        //LOGGER.log(Level.FINEST, "Type: activeXTextClass");
                                        obj = new ActiveXText();
                                        break;
                                    case "activeXTextDspClass":
                                    case "activeXTextDspClass:noedit":
                                        //LOGGER.log(Level.FINEST, "Type: activeXTextDspClass");
                                        obj = new ActiveXTextDsp();
                                        break;
                                    case "activeButtonClass":
                                        obj = new ActiveButton();
                                        break;
                                    case "activeMessageButtonClass":
                                        //LOGGER.log(Level.FINEST, "Type: activeMessageButton");
                                        obj = new ActiveMessageButton();
                                        break;
                                    case "relatedDisplayClass":
                                        //LOGGER.log(Level.FINEST, "Type: relatedDisplayClass");
                                        obj = new RelatedDisplay();
                                        break;
                                    case "shellCmdClass":
                                        obj = new ShellCommand();
                                        break;
                                    case "activeRectangleClass":
                                        //LOGGER.log(Level.FINEST, "Type: activeRectangleClass");
                                        obj = new ActiveRectangle();
                                        break;
                                    case "ByteClass":
                                        obj = new ActiveByte();
                                        break;
                                    case "activeCircleClass":
                                        obj = new ActiveCircle();
                                        break;
                                    case "activeArcClass":
                                        obj = new ActiveArc();
                                        break;
                                    case "activeChoiceButtonClass":
                                        //LOGGER.log(Level.FINEST, "Type: activeChoiceButtonClass");
                                        obj = new ActiveChoiceButton();
                                        break;
                                    case "activeLineClass":
                                        //LOGGER.log(Level.FINEST, "Type: activeLineClass");
                                        obj = new ActiveLine();
                                        break;
                                    case "activeGroupClass":
                                        obj = new ActiveGroup();
                                        if (groupStack.isEmpty()) {
                                            screenObjects.add(obj);
                                        } else {
                                            groupStack.peek().children.add(obj);
                                        }
                                        groupStack.push((ActiveGroup) obj);
                                        added = true;
                                        break;
                                    case "activeSymbolClass":
                                        obj = new ActiveSymbol();
                                        break;
                                    case "activeDynSymbolClass":
                                        obj = new ActiveDynamicSymbol();
                                        break;
                                    case "activeBarClass":
                                        obj = new ActiveBarMonitor();
                                        break;
                                    default:
                                        LOGGER.log(Level.FINEST, "Type: Unknown: {0}", tokens[1]);
                                        obj = new ScreenObject();
                                }

                                obj.objectId = objectId++;

                                if (!added) {
                                    if (groupStack.isEmpty()) {
                                        screenObjects.add(obj);
                                    } else {
                                        groupStack.peek().children.add(obj);
                                    }
                                }

                                if (obj instanceof ActiveSymbol) {
                                    symbolObjects.add((ActiveSymbol) obj);
                                }

                                LOGGER.log(Level.FINEST, "Handling Widget: {0}",
                                        obj.getClass().getSimpleName());

                                last = obj;
                                break;
                            case "endObjectProperties":
                                LOGGER.log(Level.FINEST, "Ending Widget: {0}",
                                        last.getClass().getSimpleName());
                                last = null;
                                break;
                            case "endGroup":
                                last = groupStack.pop();
                                LOGGER.log(Level.FINEST, "Re-Handling Widget: {0}",
                                        last.getClass().getSimpleName());
                                break;
                            case "w":
                                //LOGGER.log(Level.FINEST, "Found w");
                                last.w = Integer.parseInt(tokens[1]);
                                break;
                            case "h":
                                //LOGGER.log(Level.FINEST, "Found h");
                                last.h = Integer.parseInt(tokens[1]);
                                break;
                            case "x":
                                //LOGGER.log(Level.FINEST, "Found xValues");
                                last.x = Integer.parseInt(tokens[1]);
                                break;
                            case "y":
                                //LOGGER.log(Level.FINEST, "Found yValues");
                                last.y = Integer.parseInt(tokens[1]);
                                break;
                            case "numPoints":
                                ((ActiveLine) last).numPoints = Integer.parseInt(tokens[1]);
                                break;
                            case "xPoints":
                                ActiveLine alx = ((ActiveLine) last);

                                alx.xValues = new int[alx.numPoints];

                                for (int i = 0; i < alx.numPoints; i++) {
                                    String val = scanner.nextLine();
                                    String[] tks = val.split("\\s+");
                                    alx.xValues[i] = Integer.parseInt(tks[2]);
                                }
                                break;
                            case "yPoints":
                                ActiveLine aly = ((ActiveLine) last);

                                aly.yValues = new int[aly.numPoints];

                                for (int i = 0; i < aly.numPoints; i++) {
                                    String val = scanner.nextLine();
                                    String[] tks = val.split("\\s+");
                                    aly.yValues[i] = Integer.parseInt(tks[2]);
                                }
                                break;
                            case "arrows":
                                String arrows = stripQuotes(tokens[1]);
                                ((ActiveLine) last).startArrow = ("from".equals(arrows)
                                        || "both".equals(
                                                arrows));
                                ((ActiveLine) last).endArrow
                                        = ("to".equals(arrows) || "both".equals(arrows));
                                break;
                            case "closePolygon":
                                ((ActiveLine) last).closePolygon = true;
                                break;
                            case "startAngle":
                                ((ActiveArc) last).startAngle = Integer.parseInt(tokens[1]);
                                break;
                            case "totalAngle":
                                ((ActiveArc) last).totalAngle = Integer.parseInt(tokens[1]);
                                break;
                            case "lineStyle":
                                last.dash = "dash".equals(stripQuotes(tokens[1]));
                                break;
                            case "lineWidth":
                                last.lineWidth = Float.parseFloat(tokens[1]);
                                break;
                            case "max":
                                last.max = Float.parseFloat(stripQuotes(tokens[1]));
                                break;
                            case "min":
                                last.min = Float.parseFloat(stripQuotes(tokens[1]));
                                break;
                            case "origin":
                                last.origin = Float.parseFloat(stripQuotes(tokens[1]));
                                break;
                            case "useDisplayBg":
                                //LOGGER.log(Level.FINEST, "Found useDisplayBg");
                                last.useDisplayBg = true; // This means ignore bgColor and inherit screen background / transparent background
                                break;
                            case "invisible":
                                last.invisible = true;
                                break;
                            case "buttonType":
                                ((ActiveButton) last).push = "push".equals(stripQuotes(tokens[1]));
                                break;
                            case "3d":
                                ((HtmlScreenObject) last).threeDimensional = true;
                                break;
                            case "border":
                                last.border = true;
                                break;
                            case "autoSize":
                                last.autoSize = true;
                                break;
                            case "indicatorAlarm":
                                last.indicatorAlarm = true;
                                break;
                            case "icon":
                                ((RelatedDisplay) last).icon = true;
                                break;
                            case "bgColor":
                                //LOGGER.log(Level.FINEST, "Found bgColor");
                                Integer bgIndex = Integer.parseInt(tokens[2]);
                                EDLColor bgColor = colorList.lookup(bgIndex);
                                last.bgColor = bgColor;
                                break;
                            case "fgColor":
                                //LOGGER.log(Level.FINEST, "Found fgColor");
                                Integer fgIndex = Integer.parseInt(tokens[2]);
                                EDLColor fgColor = colorList.lookup(fgIndex);
                                last.fgColor = fgColor;
                                break;
                            case "onColor":
                                //LOGGER.log(Level.FINEST, "Found onColor");
                                Integer onIndex = Integer.parseInt(tokens[2]);
                                EDLColor onColor = colorList.lookup(onIndex);
                                last.onColor = onColor;
                                break;
                            case "offColor":
                                //LOGGER.log(Level.FINEST, "Found onColor");
                                Integer offIndex = Integer.parseInt(tokens[2]);
                                EDLColor offColor = colorList.lookup(offIndex);
                                last.offColor = offColor;
                                break;
                            case "topShadowColor":
                                //LOGGER.log(Level.FINEST, "Found topShadowColor");
                                Integer topIndex = Integer.parseInt(tokens[2]);
                                EDLColor topColor = colorList.lookup(topIndex);
                                last.topShadowColor = topColor;
                                break;
                            case "botShadowColor":
                                //LOGGER.log(Level.FINEST, "Found botShadowColor");
                                Integer botIndex = Integer.parseInt(tokens[2]);
                                EDLColor botColor = colorList.lookup(botIndex);
                                last.botShadowColor = botColor;
                                break;
                            case "lineColor":
                                //LOGGER.log(Level.FINEST, "Found lineColor");
                                Integer lcIndex = Integer.parseInt(tokens[2]);
                                EDLColor lcColor = colorList.lookup(lcIndex);
                                last.lineColor = lcColor;
                                break;
                            case "indicatorColor":
                                Integer inIndex = Integer.parseInt(tokens[2]);
                                EDLColor inColor = colorList.lookup(inIndex);
                                last.indicatorColor = inColor;
                                break;
                            case "fill":
                                //LOGGER.log(Level.FINEST, "Found fill");
                                last.fill = true; // This means ignore bgColor and inherit screen background / transparent background                     
                                break;
                            case "fillAlarm":
                                last.fillAlarm = true;
                                break;
                            case "lineAlarm":
                                last.lineAlarm = true;
                                break;
                            case "fgAlarm":
                                last.fgAlarm = true;
                                break;
                            case "bgAlarm":
                                last.bgAlarm = true;
                                break;
                            case "limitsFromDb":
                                last.limitsFromDb = true;
                                break;
                            case "fillColor":
                                Integer fcIndex = Integer.parseInt(tokens[2]);
                                EDLColor fcColor = colorList.lookup(fcIndex);
                                last.fillColor = fcColor;
                                break;
                            case "orientation":
                                last.horizontal
                                        = stripQuotes(tokens[1]).equals(
                                        "horizontal");
                                break;
                            case "format":
                                String format = stripQuotes(tokens[1]);
                                if (format.equals("decimal")) {
                                    last.decimal = true;
                                } // TODO: what about values: float, gfloat, exponential, default, string?
                                break;
                            case "value":
                                //LOGGER.log(Level.FINEST, "Found controlPv");
                                String value = scanner.nextLine();
                                value = value.trim();
                                String finalString = stripQuotes(value);

                                while (true) {
                                    value = scanner.nextLine();

                                    value = value.trim();

                                    if ("}".equals(value)) {
                                        break;
                                    }

                                    ((TextScreenObject) last).numLines++;
                                    finalString = finalString + "\n" + stripQuotes(value);
                                }

                                ((TextScreenObject) last).value = finalString;
                                break;
                            case "controlPv":
                                //LOGGER.log(Level.FINEST, "Found value");
                                if (last instanceof ActiveMessageButton) {
                                    ((ActiveMessageButton) last).destinationPv = stripQuotes(
                                            tokens[1]);
                                } else {
                                    last.controlPv = stripQuotes(tokens[1]);
                                }
                                break;
                            case "alarmPv":
                                last.alarmPv = stripQuotes(tokens[1]);
                                break;
                            case "indicatorPv":
                                last.indicatorPv = stripQuotes(tokens[1]);
                                break;
                            case "numPvs":
                                last.numPvs = Integer.parseInt(tokens[1]);
                                break;
                            case "numStates":
                                ((ActiveSymbol) last).numStates = Integer.parseInt(tokens[1]);
                                break;
                            case "numDsps":
                                ((RelatedDisplay) last).numDsps = Integer.parseInt(tokens[1]);
                                break;
                            case "controlPvs":
                                ActiveSymbol sym = ((ActiveSymbol) last);

                                while (scanner.hasNext()) {
                                    String val = scanner.nextLine();

                                    if ("}".equals(val)) {
                                        break;
                                    }

                                    String[] tks = val.split("\\s+");
                                    sym.controlPvs.add(stripQuotes(tks[2]));
                                }
                                break;
                            case "maxValues":
                                ActiveSymbol symMax = ((ActiveSymbol) last);

                                while (scanner.hasNext()) {
                                    String val = scanner.nextLine();

                                    if ("}".equals(val)) {
                                        break;
                                    }

                                    String[] tks = val.split("\\s+");
                                    int maxIndex = Integer.parseInt(tks[1]);

                                    if (maxIndex >= 0 && maxIndex <= 64) {
                                        symMax.maxValues[maxIndex] = Integer.parseInt(tks[2]);
                                    } else {
                                        LOGGER.log(Level.WARNING,
                                                "maxValues number out of range: {0}",
                                                maxIndex);
                                    }
                                }
                                break;
                            case "minValues":
                                ActiveSymbol symMin = ((ActiveSymbol) last);

                                while (scanner.hasNext()) {
                                    String val = scanner.nextLine();

                                    if ("}".equals(val)) {
                                        break;
                                    }

                                    String[] tks = val.split("\\s+");
                                    int minIndex = Integer.parseInt(tks[1]);

                                    if (minIndex >= 0 && minIndex <= 64) {
                                        symMin.minValues[minIndex] = Integer.parseInt(tks[2]);
                                    } else {
                                        LOGGER.log(Level.WARNING,
                                                "minValues number out of range: {0}",
                                                minIndex);
                                    }
                                }
                                break;
                            case "displayFileName":
                                RelatedDisplay rd = ((RelatedDisplay) last);

                                while (scanner.hasNext()) {
                                    String val = scanner.nextLine();

                                    if ("}".equals(val)) {
                                        break;
                                    }

                                    String[] tks = val.split("\"");
                                    int rdIndex = Integer.parseInt(tks[0].trim());

                                    if (rdIndex >= 0 && rdIndex <= 64) {
                                        rd.displayFileNames[rdIndex] = tks[1].trim();  //stripQuotes(tks[1].trim());
                                    } else {
                                        LOGGER.log(Level.WARNING,
                                                "RelatedDisplay filename out of range: {0}",
                                                rdIndex);
                                    }
                                }
                                break;
                            case "menuLabel":
                                RelatedDisplay rd2 = ((RelatedDisplay) last);

                                while (scanner.hasNext()) {
                                    String val = scanner.nextLine();

                                    if ("}".equals(val)) {
                                        break;
                                    }

                                    String[] tks = val.split("\"");
                                    int rdIndex = Integer.parseInt(tks[0].trim());

                                    if (rdIndex >= 0 && rdIndex <= 64) {
                                        rd2.menuLabels[rdIndex] = tks[1].trim(); //stripQuotes(tks[1].trim());
                                    } else {
                                        LOGGER.log(Level.WARNING,
                                                "RelatedDisplay filename out of range: {0}",
                                                rdIndex);
                                    }
                                }
                                break;
                            case "onLabel":
                                //LOGGER.log(Level.FINEST, "Found onLabel");
                                ((ActiveButton) last).onLabel = stripQuotes(line.substring(
                                        "onLabel".length()));
                                break;
                            case "offLabel":
                                ((ActiveButton) last).offLabel = stripQuotes(line.substring(
                                        "offLabel".length()));
                                break;
                            case "buttonLabel":
                                //LOGGER.log(Level.FINEST, "Found buttonLabel");
                                ((ActiveButton) last).buttonLabel = stripQuotes(line.substring(
                                        "buttonLabel".length()));
                                break;
                            case "font":
                                //LOGGER.log(Level.FINEST, "Found font");
                                String fontStr = stripQuotes(line.substring("font".length()));

                                //System.out.println("fontStr: " + fontStr);
                                EDLFont font;
                                try {
                                    font = parseFont(fontStr);
                                } catch (Exception e) {
                                    LOGGER.log(Level.WARNING,
                                            "Unable to parse font: {0}; using default",
                                            fontStr);
                                    font = EDMParser.DEFAULT_FONT;
                                }
                                last.font = font;
                                break;
                            case "fontAlign":
                                //LOGGER.log(Level.FINEST, "Found fontAlign");
                                String align = tokens[1];
                                ((TextScreenObject) last).align = stripQuotes(align);
                                break;
                            case "visMin":
                                last.visMin = Float.parseFloat(stripQuotes(tokens[1]));
                                break;
                            case "visMax":
                                last.visMax = Float.parseFloat(stripQuotes(tokens[1]));
                                break;
                            case "visInvert":
                                last.visInvert = true;
                                break;
                            case "visPv":
                                last.visPv = stripQuotes(line.substring("visPv".length()));
                                break;
                            case "file":
                                //LOGGER.log(Level.FINEST, "Found file: {0}", tokens[1]);
                                ((ActiveSymbol) last).file = stripQuotes(tokens[1]);
                                break;
                            case "numBits":
                                ((ActiveByte) last).bits = Integer.parseInt(tokens[1]);
                                break;
                            case "shift":
                                ((ActiveByte) last).shift = Integer.parseInt(tokens[1]);
                                break;
                            case "ctlFont":
                                String fStr = stripQuotes(line.substring("ctlFont".length()));

                                EDLFont ctlFont;
                                try {
                                    ctlFont = parseFont(fStr);
                                } catch (Exception e) {
                                    LOGGER.log(Level.WARNING,
                                            "Unable to parse font: {0}; using default",
                                            fStr);
                                    ctlFont = EDMParser.DEFAULT_FONT;
                                }
                                properties.ctlFont = ctlFont;
                                break;
                            case "btnFont":
                                String bStr = stripQuotes(line.substring("btnFont".length()));

                                EDLFont btnFont;
                                try {
                                    btnFont = parseFont(bStr);
                                } catch (Exception e) {
                                    LOGGER.log(Level.WARNING,
                                            "Unable to parse font: {0}; using default",
                                            bStr);
                                    btnFont = EDMParser.DEFAULT_FONT;
                                }
                                properties.btnFont = btnFont;
                                break;
                            case "textColor":
                                Integer textIndex = Integer.parseInt(tokens[2]);
                                EDLColor textColor = colorList.lookup(textIndex);
                                properties.textColor = textColor;
                                break;
                            case "ctlFgColor1":
                                Integer fg1Index = Integer.parseInt(tokens[2]);
                                EDLColor fg1Color = colorList.lookup(fg1Index);
                                properties.ctlFgColor1 = fg1Color;
                                break;
                            case "ctlFgColor2":
                                Integer fg2Index = Integer.parseInt(tokens[2]);
                                EDLColor fg2Color = colorList.lookup(fg2Index);
                                properties.ctlFgColor2 = fg2Color;
                                break;
                            case "ctlBgColor1":
                                Integer bg1Index = Integer.parseInt(tokens[2]);
                                EDLColor bg1Color = colorList.lookup(bg1Index);
                                properties.ctlBgColor1 = bg1Color;
                                break;
                            case "ctlBgColor2":
                                Integer bg2Index = Integer.parseInt(tokens[2]);
                                EDLColor bg2Color = colorList.lookup(bg2Index);
                                properties.ctlBgColor2 = bg2Color;
                                break;
                            case "beginObjectProperties":
                            case "major":
                            case "minor":
                            case "release":
                            case "endScreenProperties":
                            case "#":
                            case "}":
                            case "":
                            case "beginGroup":
                            case "4":
                            case "pressValue":
                            case "releaseValue":
                            case "useEnumNumeric":
                                break;
                            default:
                                LOGGER.log(Level.FINEST, "Ignoring Line: {0}", line);
                        }
                    } catch (Exception e) {
                        LOGGER.log(Level.WARNING, "Unable to parse line '" + line + "'; ignoring", e);
                    }
                }
            }

        }

        if (!isSymbolFile) { // Don't recurse more than one file deep
            for (ActiveSymbol symbol : symbolObjects) {
                try {
                    /*File symbolFile = new File(symbol.file);

                    if (!symbolFile.isAbsolute()) {
                        symbolFile = new File(edl.getParent() + File.separator + symbol.file);
                    }*/

                    Screen s = this.parse(symbol.file, colorList, true);
                    s.setScreenProperties(symbol);
                    symbol.screen = s;
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Unable to load symbol file: " + symbol.file, e);
                }
            }
        }

        return new Screen(properties, screenObjects, colorList);
    }

}
