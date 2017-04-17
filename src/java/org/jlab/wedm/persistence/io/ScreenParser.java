package org.jlab.wedm.persistence.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jlab.wedm.lifecycle.Configuration;
import org.jlab.wedm.widget.ActiveGroup;
import org.jlab.wedm.widget.ActiveSymbol;
import org.jlab.wedm.persistence.model.ColorPalette;
import org.jlab.wedm.widget.ActivePictureInPicture;
import org.jlab.wedm.widget.EmbeddedScreen;
import org.jlab.wedm.persistence.model.Screen;
import org.jlab.wedm.persistence.model.WEDMWidget;
import org.jlab.wedm.widget.ScreenProperties;
import org.jlab.wedm.widget.UnknownWidget;

public class ScreenParser extends EDLParser {

    /**
     * We want embedded screens to NOT repeat IDs - so we share the same counter across multiple
     * parse method calls.
     */
    private int objectId = 0;

    private static final Logger LOGGER = Logger.getLogger(ScreenParser.class.getName());

    public Screen parse(String name, ColorPalette colorList, int recursionLevel) throws
            FileNotFoundException, IOException {

        File edl = getEdlFile(name);
        
        String canonicalPath = edl.getCanonicalPath();

        ScreenProperties properties = new ScreenProperties();
        properties.colorList = colorList;
        List<WEDMWidget> screenObjects = new ArrayList<>();
        List<EmbeddedScreen> embeddedScreens = new ArrayList<>();

        try (Scanner scanner = new Scanner(edl)) {

            WEDMWidget last = null;
            Map<String, String> traits = new HashMap<>();
            Deque<ActiveGroup> groupStack = new ArrayDeque<>();
            Deque<Map<String, String>> groupTraitStack = new ArrayDeque();

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
                                traits = new HashMap<>();
                            break;                       
                            case "object":
                                //LOGGER.log(Level.FINEST, "Found: object");

                                WEDMWidget obj;

                                String className = Configuration.CLASS_MAP.get(tokens[1]);

                                if (className == null) {
                                    LOGGER.log(Level.WARNING, "Unknown EDM Class: {0}", tokens[1]);
                                    obj = new UnknownWidget();
                                } else {
                                    try {
                                        Class<?> clazz = Class.forName(className);
                                        Constructor<?> constructor = clazz.getConstructor();
                                        obj = (WEDMWidget) constructor.newInstance();
                                    } catch (ClassNotFoundException e) {
                                        LOGGER.log(Level.WARNING,
                                                "EDM Class definition not in classpath: {0}",
                                                className);
                                        obj = new UnknownWidget();
                                    } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                                        LOGGER.log(Level.WARNING, "Unable to create EDM Class", e);
                                        obj = new UnknownWidget();
                                    }
                                }
                                
                                traits = new HashMap<>();                              
                                
                                if (obj instanceof ActiveGroup) {
                                    if (groupStack.isEmpty()) {
                                        screenObjects.add(obj);
                                    } else {
                                        groupStack.peek().children.add(obj);
                                    }
                                    groupStack.push((ActiveGroup) obj);
                                    groupTraitStack.push(traits);
                                    added = true;
                                }

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
                                traits.put("WEDM_WIDGET_ID", String.valueOf(objectId++));                                  
                                break;
                            //case "beginObjectProperties":
                            //    break;                                         
                            case "endScreenProperties":
                            case "endObjectProperties":
                                //LOGGER.log(Level.FINEST, "Ending Widget: {0}",
                                //        last.getClass().getSimpleName());
                                last.parseTraits(traits, colorList);
                                last.performColorRuleCorrection();
                                last = null;
                                traits = null;
                                break;
                            case "endGroup":
                                last = groupStack.pop();
                                traits = groupTraitStack.pop();
                                //LOGGER.log(Level.FINEST, "Re-Handling Widget: {0}",
                                //        last.getClass().getSimpleName());
                                break;
                            
                                
                                
                                /*

                                
                                
                                
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
                            case "textColor":
                                Integer textIndex = Integer.parseInt(tokens[2]);
                                EDLColor textColor = colorList.lookup(textIndex);
                                traits.textColor = textColor;
                                break;
                            case "ctlFgColor1":
                                Integer fg1Index = Integer.parseInt(tokens[2]);
                                EDLColor fg1Color = colorList.lookup(fg1Index);
                                traits.ctlFgColor1 = fg1Color;
                                break;
                            case "ctlFgColor2":
                                Integer fg2Index = Integer.parseInt(tokens[2]);
                                EDLColor fg2Color = colorList.lookup(fg2Index);
                                traits.ctlFgColor2 = fg2Color;
                                break;
                            case "ctlBgColor1":
                                Integer bg1Index = Integer.parseInt(tokens[2]);
                                EDLColor bg1Color = colorList.lookup(bg1Index);
                                traits.ctlBgColor1 = bg1Color;
                                break;
                            case "ctlBgColor2":
                                Integer bg2Index = Integer.parseInt(tokens[2]);
                                EDLColor bg2Color = colorList.lookup(bg2Index);
                                traits.ctlBgColor2 = bg2Color;
                                break;
                            case "2ndBgColor":
                                Integer back2Index = Integer.parseInt(tokens[2]);
                                EDLColor back2Color = colorList.lookup(back2Index);
                                ((ActiveMotifSlider) last).secondBgColor = back2Color;
                                break;*/
                            default:
                                if (!line.isEmpty()) { // Skip blank lines
                                    String value = "";

                                    if (line.trim().startsWith("#")) {
                                        // Ignoring comment
                                    } else {
                                        if (line.trim().endsWith("{")) {
                                            String subline = scanner.nextLine();
                                            subline = subline.trim();
                                            String finalString = stripQuotes(subline);

                                            while (true) {
                                                subline = scanner.nextLine();

                                                subline = subline.trim();

                                                if ("}".equals(subline)) {
                                                    break;
                                                }
                                                finalString = finalString + "\n" + stripQuotes(
                                                        subline);
                                            }

                                            value = finalString;
                                        } else {
                                            value = stripQuotes(line.substring(
                                                    tokens[0].length()));
                                        }

                                        traits.put(tokens[0], value);
                                    }
                                }
                        }
                    } catch (Exception e) {
                        LOGGER.log(Level.WARNING, "Unable to parse line '" + line + "'; file '"
                                + canonicalPath + "'; ignoring", e);
                    }
                }
            } // end while line
        } // end scanner try with resources // end scanner try with resources

        if (recursionLevel < 5) { // Don't recurse more than five files deep
            for (EmbeddedScreen embedded : embeddedScreens) {

                //LOGGER.log(Level.FINEST, "Embedded file: {0}", embedded.file);
                try {

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
                            if (embedded.numDsps > 0 && embedded.numDsps <= TraitParser.MAX_ARRAY_SIZE) {
                                for (int i = 0; i < embedded.numDsps; i++) {
                                    String f = embedded.displayFileName[i];

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
}
