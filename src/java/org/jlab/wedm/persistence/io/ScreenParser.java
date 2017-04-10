package org.jlab.wedm.persistence.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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
import org.jlab.wedm.persistence.model.html.ActiveStaticText;
import org.jlab.wedm.persistence.model.html.ActiveControlText;
import org.jlab.wedm.persistence.model.ColorList;
import org.jlab.wedm.persistence.model.EDLColor;
import org.jlab.wedm.persistence.model.EDLFont;
import org.jlab.wedm.persistence.model.html.HtmlScreenObject;
import org.jlab.wedm.persistence.model.html.RelatedDisplay;
import org.jlab.wedm.persistence.model.ActivePictureInPicture;
import org.jlab.wedm.persistence.model.EDLColorRule;
import org.jlab.wedm.persistence.model.EmbeddedScreen;
import org.jlab.wedm.persistence.model.Screen;
import org.jlab.wedm.persistence.model.ScreenObject;
import org.jlab.wedm.persistence.model.ScreenProperties;
import org.jlab.wedm.persistence.model.html.ActiveImage;
import org.jlab.wedm.persistence.model.html.ActiveMenuButton;
import org.jlab.wedm.persistence.model.html.ActiveMessageButton;
import org.jlab.wedm.persistence.model.html.ActiveRegExText;
import org.jlab.wedm.persistence.model.html.ActiveUpdateText;
import org.jlab.wedm.persistence.model.html.ShellCommand;
import org.jlab.wedm.persistence.model.html.TextScreenObject;
import org.jlab.wedm.persistence.model.svg.ActiveArc;
import org.jlab.wedm.persistence.model.svg.ActiveBarMonitor;
import org.jlab.wedm.persistence.model.svg.ActiveByte;
import org.jlab.wedm.persistence.model.svg.ActiveCircle;
import org.jlab.wedm.persistence.model.svg.ActiveRectangle;

public class ScreenParser extends EDMParser {

    /**
     * We want embedded screens to NOT repeat IDs - so we share the same counter across multiple
     * parse method calls.
     */
    private int objectId = 0;

    private static final Logger LOGGER = Logger.getLogger(ScreenParser.class.getName());

    public Screen parse(String name, ColorList colorList, int recursionLevel) throws
            FileNotFoundException, IOException {

        File edl = getEdlFile(name);

        String canonicalPath = edl.getCanonicalPath();

        ScreenProperties properties = new ScreenProperties();
        List<ScreenObject> screenObjects = new ArrayList<>();
        List<EmbeddedScreen> embeddedScreens = new ArrayList<>();

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
                                        obj = new ActiveStaticText();
                                        break;
                                    case "activeXRegTextClass":
                                        obj = new ActiveRegExText();
                                        break;
                                    case "activeXTextDspClass": // CONTROL
                                    case "activeXTextDspClass:noedit": // MONITOR
                                        //LOGGER.log(Level.FINEST, "Type: activeXTextDspClass");
                                        obj = new ActiveControlText();
                                        break;
                                    case "TextupdateClass":
                                        obj = new ActiveUpdateText();
                                        break;
                                    case "activeButtonClass":
                                        obj = new ActiveButton();
                                        break;
                                    case "activeMessageButtonClass":
                                        //LOGGER.log(Level.FINEST, "Type: activeMessageButton");
                                        obj = new ActiveMessageButton();
                                        break;
                                    case "activeMenuButtonClass":
                                        obj = new ActiveMenuButton();
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
                                    case "activePipClass":
                                        obj = new ActivePictureInPicture();
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
                                    case "activePngClass":
                                    case "cfcf6c8a_dbeb_11d2_8a97_00104b8742df":
                                        obj = new ActiveImage();
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

                                if (obj instanceof EmbeddedScreen) {
                                    embeddedScreens.add((EmbeddedScreen) obj);
                                }

                                //LOGGER.log(Level.FINEST, "Handling Widget: {0}",
                                //        obj.getClass().getSimpleName());
                                last = obj;
                                break;
                            case "endObjectProperties":
                                //LOGGER.log(Level.FINEST, "Ending Widget: {0}",
                                //        last.getClass().getSimpleName());
                                last = null;
                                break;
                            case "endGroup":
                                last = groupStack.pop();
                                //LOGGER.log(Level.FINEST, "Re-Handling Widget: {0}",
                                //        last.getClass().getSimpleName());
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
                                    String[] tks = val.trim().split("\\s");
                                    alx.xValues[i] = Integer.parseInt(tks[1].trim());
                                }
                                break;
                            case "yPoints":
                                ActiveLine aly = ((ActiveLine) last);

                                aly.yValues = new int[aly.numPoints];

                                for (int i = 0; i < aly.numPoints; i++) {
                                    String val = scanner.nextLine();
                                    String[] tks = val.trim().split("\\s");
                                    aly.yValues[i] = Integer.parseInt(tks[1].trim());
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
                            case "noScroll":
                                ((ActivePictureInPicture) last).noscroll = true;
                                break;
                            case "center":
                                ((ActivePictureInPicture) last).center = true;
                                break;
                            case "invisible":
                                last.invisible = true;
                                break;
                            case "motifWidget":
                                last.motifWidget = true;
                                break;
                            case "useOriginalSize":
                                ((ActiveSymbol) last).useOriginalSize = true;
                                break;
                            case "useOriginalColors":
                                ((ActiveSymbol) last).useOriginalColors = true;
                                break;
                            case "buttonType": // ActiveButton looks for buttonType: "push" and has default of toggle
                                ((ActiveButton) last).push = "push".equals(stripQuotes(tokens[1]));
                                break;
                            case "toggle": // ActiveMessageButton looks for toggle and has default of push
                                ((ActiveMessageButton) last).push = false;
                                break;
                            case "pressValue":
                                ((ActiveButton) last).pressValue = stripQuotes(line.substring(
                                        "pressValue".length()));
                                break;
                            case "releaseValue":
                                ((ActiveButton) last).releaseValue = stripQuotes(line.substring(
                                        "releaseValue".length()));
                                break;
                            case "3d":
                                ((HtmlScreenObject) last).threeDimensional = true;
                                break;
                            case "showUnits":
                                ((ActiveControlText) last).showUnits = true;
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
                            case "swapButtons":
                                ((ActiveButton) last).swapButtons = true;
                                break;
                            case "editable":
                                last.editable = true;
                                break;
                            case "useHexPrefix":
                                last.useHexPrefix = true;
                                break;
                            case "useAlarmBorder":
                                ((ActiveControlText) last).useAlarmBorder = true;
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
                                last.format = stripQuotes(tokens[1]);
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
                                // ActiveMessageButton GUI tool calls this "destinationPv" on the interface.
                                last.controlPv = stripQuotes(line.substring("controlPv".length()));
                                break;
                            case "colorPv": // ActiveButton uses colorPv; all others seem to use alarmPv;  alarmPv acts as alarm or color PV based other config
                            case "alarmPv":
                                last.alarmPv = stripQuotes(line.substring("alarmPv".length())); // this works because colorPv has same length of alarmPv
                                break;
                            case "indicatorPv": // We don't simply stripQuotes(tokens[1]); because PV name sometimes has spaces (LOC// <space> NAME) is acceptable
                                last.indicatorPv = stripQuotes(
                                        line.substring("indicatorPv".length()));
                                break;
                            case "numPvs":
                                last.numPvs = Integer.parseInt(tokens[1]);
                                break;
                            case "precision": // Quotes are usually not there, but sometimes are
                                last.precision = Integer.parseInt(stripQuotes(tokens[1]));
                                break;
                            case "numStates":
                                ((ActiveSymbol) last).numStates = Integer.parseInt(tokens[1]);
                                break;
                            case "numDsps": // RelatedDisplay and PictureInPicture
                                last.numDsps = Integer.parseInt(tokens[1]);
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
                                while (scanner.hasNext()) {
                                    String val = scanner.nextLine();

                                    if ("}".equals(val)) {
                                        break;
                                    }

                                    String[] tks = val.trim().split("\\s");
                                    int rdIndex = Integer.parseInt(tks[0].trim());

                                    if (rdIndex >= 0 && rdIndex <= 64) {
                                        last.displayFileNames[rdIndex] = stripQuotes(val.substring(
                                                val.indexOf(tks[0]) + tks[0].length()));
                                    } else {
                                        LOGGER.log(Level.WARNING,
                                                "RelatedDisplay filename out of range: {0}",
                                                rdIndex);
                                    }
                                }
                                break;
                            case "menuLabel": // Quotes are optional so parsing is harder than it has to be
                                while (scanner.hasNext()) {
                                    String val = scanner.nextLine();

                                    if ("}".equals(val)) {
                                        break;
                                    }

                                    String[] tks = val.trim().split("\\s");
                                    int rdIndex = Integer.parseInt(tks[0].trim());

                                    if (rdIndex >= 0 && rdIndex <= 64) {
                                        last.menuLabels[rdIndex] = stripQuotes(val.substring(
                                                val.indexOf(tks[0]) + tks[0].length()));
                                    } else {
                                        LOGGER.log(Level.WARNING,
                                                "menuLabel filename out of range: {0}",
                                                rdIndex);
                                    }
                                }
                                break;
                            case "symbols":
                                while (scanner.hasNext()) {
                                    String val = scanner.nextLine();

                                    if ("}".equals(val)) {
                                        break;
                                    }

                                    String[] tks = val.trim().split("\\s");
                                    int rdIndex = Integer.parseInt(tks[0].trim());

                                    if (rdIndex >= 0 && rdIndex <= 64) {
                                        last.symbols[rdIndex] = stripQuotes(val.substring(
                                                val.indexOf(tks[0]) + tks[0].length()));
                                    } else {
                                        LOGGER.log(Level.WARNING,
                                                "symbols (menu macro list) out of range: {0}",
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
                            case "filePv":
                                ((ActivePictureInPicture) last).filePv = stripQuotes(line.substring(
                                        "filePv".length()));
                                break;
                            case "file":
                                if (last instanceof ActiveImage) {
                                    ((ActiveImage) last).file = stripQuotes(tokens[1]);
                                } else {
                                    ((EmbeddedScreen) last).file = stripQuotes(tokens[1]);
                                }
                                break;
                            case "displaySource":
                                ((EmbeddedScreen) last).displaySource = stripQuotes(tokens[1]);
                                break;
                            case "numBits":
                                ((ActiveByte) last).bits = Integer.parseInt(tokens[1]);
                                break;
                            case "shift":
                                ((ActiveByte) last).shift = Integer.parseInt(tokens[1]);
                                break;
                            case "endian":
                                ((ActiveByte) last).littleEndian = "little".equals(stripQuotes(
                                        tokens[1]));
                                break;
                            case "title":
                                properties.title = stripQuotes(line.substring("title".length()));
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
                            case "id":
                            case "fillMode":
                            case "symbolTag":
                            case "symbol0":
                            case "value0":
                            case "replaceSymbols":
                            case "beginObjectProperties":
                            case "major":
                            case "minor":
                            case "release":
                            case "endScreenProperties":
                            case "objType":
                            case "noExecuteClipMask":
                            case "newPos":
                            case "fastUpdate":
                            case "smartRefresh":
                            case "fieldLen":
                            case "numCmds": // ShellCommand not supported
                            case "command":
                            case "commandLabel":
                            case "autoHeight":
                            case "nullColor": // TODO: support null color at some point
                            case "#":
                            case "}":
                            case "":
                            case "beginGroup":
                            case "4":
                            case "useEnumNumeric":
                            case "includeHelpIcon":
                            case "selectColor":
                            case "inconsistentColor":
                            case "allowDups":
                            case "noEdit":
                            case "gridSize":
                            case "execCursor":
                            case "scaleMax":
                            case "scaleMin":
                            case "controlLabelType":
                            case "increment":
                            case "2ndBgColor":
                            case "snapToGrid":
                            case "setPosition":
                            case "setSize":
                            case "sizeOfs":
                            case "ignoreMultiplexors":
                            case "helpCommand":
                            case "multipleInstances":
                            case "labelTicks":
                            case "majorTicks":
                            case "scaleFormat":
                            case "yPosOffset":
                            case "xPosOffset":
                            case "propagateMacros":
                            case "labelType":
                            case "numItems":
                            case "inputFocusUpdates":
                            case "nullCondition":
                            case "pv":
                                break;
                            default:
                                LOGGER.log(Level.FINEST, "Ignoring Line: {0}", line);
                        }
                    } catch (Exception e) {
                        LOGGER.log(Level.WARNING, "Unable to parse line '" + line + "'; file '"
                                + canonicalPath + "'; ignoring", e);
                    }
                }
            } // end while line
        } // end scanner try with resources

        // Make sure any color rules with no PV result in first color of rule
        doColorCheckRecursive(colorList, screenObjects);

        if (recursionLevel < 5) { // Don't recurse more than five files deep
            for (EmbeddedScreen embedded : embeddedScreens) {

                //LOGGER.log(Level.FINEST, "Embedded file: {0}", embedded.file);
                try {
                    /*File symbolFile = new File(symbol.file);

                    if (!symbolFile.isAbsolute()) {
                        symbolFile = new File(edl.getParent() + File.separator + symbol.file);
                    }*/

                    if (embedded instanceof ActiveSymbol) {
                        if (embedded.file != null) {
                            Screen s = this.parse(embedded.file, colorList, recursionLevel + 1);
                            s.setScreenProperties(embedded);
                            embedded.screen = s;
                        } else {
                            LOGGER.log(Level.WARNING, "Symbol with no file: {0}", embedded.objectId);
                        }
                    } else if (embedded instanceof ActivePictureInPicture) {
                        if (embedded.file != null && "file".equals(embedded.displaySource)) {
                            Screen s = this.parse(embedded.file, colorList, recursionLevel + 1);
                            s.setScreenProperties(embedded);
                            embedded.screen = s;
                        } else if ("menu".equals(embedded.displaySource)) { // Use filePv to determine which menu item to use
                            if (embedded.numDsps > 0 && embedded.numDsps <= 64) {
                                for (int i = 0; i < embedded.numDsps; i++) {
                                    String f = embedded.displayFileNames[i];

                                    //LOGGER.log(Level.FINEST, "file {0}: {1}", new Object[]{i, f});
                                    if (f != null) {
                                        try {
                                            Screen s2 = this.parse(f, colorList, recursionLevel + 1);

                                            s2.embeddedIndex = i;

                                            ((ActivePictureInPicture) embedded).screenList.add(s2);
                                        } catch (Exception e) {
                                            LOGGER.log(Level.WARNING,
                                                    "Unable to load embedded menu file from: "
                                                    + canonicalPath, e);
                                        }
                                    }
                                }
                            }
                        } else { // We don't currently handle embedded with source file name from PV
                            LOGGER.log(Level.WARNING, "Unknown PIP: {0}", embedded.objectId);
                        }
                    } else {
                        LOGGER.log(Level.WARNING, "Unknown embedded type: {0}", embedded.objectId);
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Unable to load embedded file: " + embedded.file, e);
                }
            }
        }

        return new Screen(canonicalPath, properties, screenObjects, colorList);
    }

    private void doColorCheckRecursive(ColorList colorList, List<ScreenObject> screenObjects) {
        for (ScreenObject obj : screenObjects) {
            if (obj instanceof ActiveGroup) {
                ActiveGroup grp = (ActiveGroup) obj;
                doColorCheckRecursive(colorList, grp.children);
            } else {
                checkForColorRuleWithNoPv(colorList, obj);
            }
        }
    }

    private void checkForColorRuleWithNoPv(ColorList colorList, ScreenObject obj) {
        String name;

        if (obj.alarmPv == null) {
            if (obj.lineColor != null && obj.lineColor instanceof EDLColorRule) {
                name = ((EDLColorRule) obj.lineColor).getFirstColor();
                obj.lineColor = colorList.lookup(name);
            }

            if (obj.fill && obj.fillColor != null
                    && obj.fillColor instanceof EDLColorRule) {
                name = ((EDLColorRule) obj.fillColor).getFirstColor();
                obj.fillColor = colorList.lookup(name);
            }

            if (obj.fgColor != null && obj.fgColor instanceof EDLColorRule) {
                name = ((EDLColorRule) obj.fgColor).getFirstColor();
                obj.fgColor = colorList.lookup(name);
            }

            if (obj.onColor != null && obj.onColor instanceof EDLColorRule) {
                name = ((EDLColorRule) obj.onColor).getFirstColor();
                obj.onColor = colorList.lookup(name);
            }

            if (obj.offColor != null && obj.offColor instanceof EDLColorRule) {
                name = ((EDLColorRule) obj.offColor).getFirstColor();
                obj.offColor = colorList.lookup(name);
            }
        }
    }
}
