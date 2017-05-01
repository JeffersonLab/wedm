package org.jlab.wedm.persistence.io;

import java.io.File;
import org.jlab.wedm.persistence.model.EDLFont;

/**
 *
 * @author ryans
 */
public class EDLParser {

    public static final String EDL_ROOT_DIR;
    public static final String REWRITE_FROM_DIR;
    public static final String REWRITE_TO_DIR;
    public static final EDLFont DEFAULT_FONT = new EDLFont("helvetica", false, false, 12);

    static {
        final String defaultRoot = "C:\\EDL";
        String root = System.getenv("EDL_DIR");
        if (root == null) {
            root = defaultRoot;
        }

        EDL_ROOT_DIR = root;

        REWRITE_FROM_DIR = System.getenv("REWRITE_FROM_DIR");
        REWRITE_TO_DIR = System.getenv("REWRITE_TO_DIR");
    }

    public static String rewriteFileName(String name) {
        if (REWRITE_FROM_DIR != null && REWRITE_TO_DIR != null) {
            if (name.startsWith(REWRITE_FROM_DIR)) {
                name = name.substring(REWRITE_FROM_DIR.length());
                name = REWRITE_TO_DIR + name;
            }
        }
        
        return name;
    }

    public static File getEdlFile(String name) {
        if (name == null) {
            throw new RuntimeException("An EDL file is required");
        }

        if (!name.endsWith(".edl")) {
            name = name + ".edl";
        }

        name = EDLParser.rewriteFileName(name);

        File edl = new File(name);

        if (!edl.isAbsolute()) {
            edl = new File(EDL_ROOT_DIR + File.separator + name);
        }

        return edl;
    }

    public static String stripQuotes(String value) {

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

    /*protected EDLFont parseFont(String fontStr) {
        String[] tokens = fontStr.split("-");

        String name = tokens[0];
        String weight = tokens[1]; // bold
        String oblique = tokens[2]; // italic
        Float size = Float.parseFloat(tokens[3]);

        return new EDLFont(name, "bold".equals(weight), "o".equals(oblique) || "i".equals(oblique),
                size);
    }*/
}
