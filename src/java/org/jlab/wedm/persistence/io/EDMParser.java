package org.jlab.wedm.persistence.io;

import org.jlab.wedm.persistence.model.EDLFont;

/**
 *
 * @author ryans
 */
public class EDMParser {

    public static final String EDL_ROOT_DIR;
    public static final EDLFont DEFAULT_FONT = new EDLFont("helvetica", false, false, 12);

    static {
        final String defaultValue = "C:\\EDL";
        String value = System.getenv("EDL_DIR");
        if (value == null) {
            value = defaultValue;
        }

        EDL_ROOT_DIR = value;
    }

    protected String stripQuotes(String value) {

        value = value.trim();

        if (value.startsWith("\"")) {
            value = value.substring(1);
        }

        if (value.endsWith("\"")) {
            value = value.substring(0, value.length() - 1);
        }

        // Replace escaped double quotes \" with just double quotes
        value = value.replace("\\" + "\"", "\"");

        // Replace escaped left and right bracket (this is weird, but sometimes happens in EDL)
        value = value.replace("\\" + "{", "{");
        value = value.replace("\\" + "}", "}");

        return value;
    }

    protected int downsampleRgb65kTo256(int x) {
        int min = 0;
        int max = 0x10000;
        int a = 0;
        int b = 256;

        return ((b * (x - min)) / max) + a;
    }

    protected EDLFont parseFont(String fontStr) {
        String[] tokens = fontStr.split("-");

        String name = tokens[0];
        String weight = tokens[1]; // bold
        String oblique = tokens[2]; // italic
        Float size = Float.parseFloat(tokens[3]);

        return new EDLFont(name, "bold".equals(weight), "o".equals(oblique) || "i".equals(oblique),
                size);
    }
}
