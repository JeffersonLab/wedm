package org.jlab.wedm.persistence.io;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jlab.wedm.persistence.model.ColorPalette;
import org.jlab.wedm.persistence.model.EDLColor;
import org.jlab.wedm.persistence.model.EDLFont;

/**
 *
 * @author ryans
 */
public class TraitParser {

    private static final Logger LOGGER = Logger.getLogger(TraitParser.class.getName());

    public static final int MAX_ARRAY_SIZE = 64;

    public static EDLFont parseFont(Map<String, String> traits, String key, EDLFont defaultValue) {
        String value = traits.get(key);
        EDLFont f = defaultValue;
        try {
            if (value != null) {
                String[] tokens = value.split("-");

                String name = tokens[0];
                String weight = tokens[1]; // bold
                String oblique = tokens[2]; // italic
                Float size = Float.parseFloat(tokens[3]);

                f = new EDLFont(name, "bold".equals(weight), "o".equals(oblique) || "i".equals(
                        oblique),
                        size);
            }
        } catch (Exception e) {
            LOGGER.log(Level.FINEST, "Unable to parse font; key: " + key + "; value: " + value, e);
        }
        return f;
    }

    public static boolean parseBoolean(Map<String, String> traits, String key) {
        boolean result = false;
        String value = traits.get(key);
        try {
            if (value != null) {
                result = true;
            }
        } catch (Exception e) {
            LOGGER.log(Level.FINEST, "Unable to parse Boolean; key: " + key + "; value: " + value, e);
        }

        return result;
    }

    public static EDLColor parseColor(Map<String, String> traits, ColorPalette palette, String key,
            EDLColor defaultValue) {
        String value = traits.get(key);
        EDLColor result = defaultValue;

        try {
            if (value != null) {
                String[] tokens = value.split("\\s+");
                Integer index = Integer.parseInt(tokens[1]);
                result = palette.lookup(index);
            }
        } catch (Exception e) {
            LOGGER.log(Level.FINEST, "Unable to parse color; key: " + key + "; value: " + value, e);
        }

        return result;
    }

    public static Float parseFloat(Map<String, String> traits, String key, Float defaultValue) {
        String value = traits.get(key);
        Float result = defaultValue;
        try {
            if (value != null) {
                result = Float.parseFloat(value);
            }
        } catch (NumberFormatException e) {
            LOGGER.log(Level.FINEST, "Unable to parse int; key: " + key + "; value: " + value, e);
        }

        return result;
    }

    public static Integer parseInt(Map<String, String> traits, String key, Integer defaultValue) {
        String value = traits.get(key);
        Integer result = defaultValue;
        try {
            if (value != null) {
                result = Integer.parseInt(value);
            }
        } catch (NumberFormatException e) {
            LOGGER.log(Level.FINEST, "Unable to parse int; key: " + key + "; value: " + value, e);
        }

        return result;
    }

    public static int[] parseIntArray(Map<String, String> traits, int elementCount, String key) {
        String value = traits.get(key);
        int[] result = null;
        try {
            if (value != null) {
                String[] lines = value.split("\n");

                /*if (lines.length != elementCount) {
                    throw new IllegalArgumentException("Number of points does not match: "
                            + elementCount + " vs " + lines.length);
                }*/
                // Initialized to all zeros, which is actually important and leveraged
                result = new int[elementCount];

                for (int i = 0; i < lines.length; i++) {
                    String[] tks = lines[i].split("\\s+");
                    int index = Integer.parseInt(tks[0]);

                    if (index >= 0 && index <= MAX_ARRAY_SIZE) {
                        result[index] = Integer.parseInt(tks[1]);
                    } else {
                        throw new IllegalArgumentException(
                                "index number out of range: " + index);
                    }
                }

                /*for (int i = 0; i < elementCount; i++) {
                    String val = lines[i];
                    String[] tks = val.trim().split("\\s");
                    result[i] = Integer.parseInt(tks[1].trim());
                }*/
            }
        } catch (Exception e) {
            LOGGER.log(Level.FINEST, "Unable to parse int; key: " + key + "; value: " + value, e);
        }

        return result;
    }

    public static String[] parseStringArray(Map<String, String> traits, int elementCount,
            String key) {
        String value = traits.get(key);
        String[] result = null;
        try {
            if (value != null) {
                String[] lines = value.split("\n");

                /*if (lines.length != elementCount) {
                    throw new IllegalArgumentException("Number of points does not match: "
                            + elementCount + " vs " + lines.length);
                }*/
                result = new String[elementCount];

                for (int i = 0; i < lines.length; i++) {
                    String[] tks = lines[i].split("\\s+");
                    int index = Integer.parseInt(tks[0]);

                    if (index >= 0 && index <= MAX_ARRAY_SIZE) {
                        // value is stripQuotes by parser, but double quotes in middle still there
                        String val = tks[1].trim();

                        if (val.startsWith("\"")) {
                            val = val.substring(1);
                        }

                        result[index] = val;
                    } else {
                        throw new IllegalArgumentException(
                                "index number out of range: " + index);
                    }
                }

                /*for (int i = 0; i < elementCount; i++) {
                    String val = lines[i];
                    String[] tks = val.trim().split("\\s");
                    result[i] = Integer.parseInt(tks[1].trim());
                }*/
            }
        } catch (Exception e) {
            LOGGER.log(Level.FINEST, "Unable to parse String; key: " + key + "; value: " + value, e);
        }

        return result;
    }
}
