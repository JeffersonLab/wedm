package org.jlab.wedm.persistence.io;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLConnection;
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
import org.jlab.wedm.persistence.model.ColorPalette;
import org.jlab.wedm.persistence.model.Screen;
import org.jlab.wedm.persistence.model.WEDMWidget;
import org.jlab.wedm.widget.ActiveGroup;
import org.jlab.wedm.widget.ActivePictureInPicture;
import org.jlab.wedm.widget.ActiveSymbol;
import org.jlab.wedm.widget.EmbeddedScreen;
import org.jlab.wedm.widget.ScreenProperties;
import org.jlab.wedm.widget.UnknownWidget;

public class ScreenParser extends EDLParser {

    /**
     * We want embedded screens to NOT repeat IDs - so we share the same counter
     * across multiple parse method calls.
     */
    private int objectId = 0;

    private static final Logger LOGGER = Logger.getLogger(ScreenParser.class.getName());

    public Screen parse(String name, ColorPalette colorList, int recursionLevel) throws
            FileNotFoundException, IOException {

        int max_recurse = 5;

        URL edl = getEdlURL(name);

        URLConnection edl_conn = edl.openConnection();
        edl_conn.connect();
        String url = edl.toString();

        /* Limit the number of embedded files to 1 file deep if the resource is remote. */
        if (url.startsWith("file:"))
            max_recurse = 5;
        else
            max_recurse = 1;

        long modifiedDate = edl_conn.getLastModified();

        InputStream edl_input = edl_conn.getInputStream();

        ScreenProperties properties = new ScreenProperties();
        properties.colorList = colorList;
        List<WEDMWidget> screenObjects = new ArrayList<>();
        List<EmbeddedScreen> embeddedScreens = new ArrayList<>();

        try (Scanner scanner = new Scanner(edl_input)) {

            WEDMWidget last = null;
            Map<String, String> traits = new HashMap<>();
            Deque<ActiveGroup> groupStack = new ArrayDeque<>();
            Deque<Map<String, String>> groupTraitStack = new ArrayDeque<>();

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
                            case "beginObjectProperties":
                                if (traits == null) { // additional properties! (RegTextupdateClass for example)
                                    traits = last.getTraits(); // Hack, alternatively we could create new and update parseTraits to be addTraits?
                                }
                                break;
                            case "endScreenProperties":
                            case "endObjectProperties":
                                //LOGGER.log(Level.FINEST, "Ending Widget: {0}",
                                //        last.getClass().getSimpleName());
                                last.parseTraits(traits, properties);
                                last.performColorRuleCorrection();
                                traits = null;
                                //last = null; // We can no longer clear last obj since widgets like RegTextupdateClass have multiple sets of properties
                                break;
                            case "endGroup":
                                last = groupStack.pop();
                                traits = groupTraitStack.pop();
                                //LOGGER.log(Level.FINEST, "Re-Handling Widget: {0}",
                                //        last.getClass().getSimpleName());
                                break;
                            default:
                                if (!line.isEmpty()) { // Skip blank lines
                                    String value = "";

                                    if (line.trim().startsWith("#")) {
                                        // Ignoring comment
                                    } else {
                                        if (line.trim().endsWith("{")) {
                                            // Special case: version 4.0.0 of ActiveLine sometimes
                                            // has { after numPoints.  It also sometimes ends the
                                            // bracket on the line after yPoints and sometimes not.
                                            // If so we end up with a harmless trait named } with no
                                            // value.
                                            // FYI - version 4.0.1 of ActiveLine doesn't have this
                                            // problem
                                            if (line.startsWith("numPoints")) {
                                                value = line.split(" ")[1];
                                            } else { // "Normal" bracket behavior (non-nested)
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
                                            }
                                        } else {
                                            value = stripQuotes(line.substring(
                                                    tokens[0].length()));
                                            //System.out.println("trait: " + tokens[0] + "; value: " + value);
                                        }

                                        traits.put(tokens[0], value);
                                    }
                                }
                        }
                    } catch (Exception e) {
                        LOGGER.log(Level.WARNING, "Unable to parse line '" + line + "'; file '"
                                + url + "'; ignoring", e);
                    }
                }
            } // end while line
        } // end scanner try with resources // end scanner try with resources

        if (recursionLevel < max_recurse) { // Don't recurse more than five files deep
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
                            if (embedded.numDsps > 0 && embedded.numDsps
                                    <= TraitParser.MAX_ARRAY_SIZE) {
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
                                                    + url, e);
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

        return new Screen(url, modifiedDate, properties, screenObjects, colorList);
    }
}
