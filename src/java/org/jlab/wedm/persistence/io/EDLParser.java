package org.jlab.wedm.persistence.io;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jlab.wedm.persistence.model.EDLFont;

/**
 *
 * @author slominskir
 */
public class EDLParser {

    private static final Logger LOGGER = Logger.getLogger(EDLParser.class.getName());

    public static final EDLFont DEFAULT_FONT = new EDLFont("helvetica", false, false, 12);
    public static final String EDL_ROOT_DIR;
    public static final String HTTP_DOC_ROOT;
    public static final String REWRITE_FROM_DIR;
    public static final String REWRITE_TO_DIR;
    public static final String[] SEARCH_PATH;

    /**
     * On Windows you could set EDL_DIR to a remote ExpanDrive mount say
     * E:\cs\opshome\edm then set REWRITE_FROM_DIR to /
     * and REWRITE_TO_DIR to E:\.
     **/

    static {
        final String defaultRoot = "C:\\EDL";
        String root = System.getenv("EDL_DIR");
        if (root == null) {
            root = defaultRoot;
        }

        EDL_ROOT_DIR = root;

        REWRITE_FROM_DIR = System.getenv("REWRITE_FROM_DIR");
        REWRITE_TO_DIR = System.getenv("REWRITE_TO_DIR");

        HTTP_DOC_ROOT = System.getenv("EDMHTTPDOCROOT");
        String search_path = System.getenv("EDMDATAFILES");
        if (null != search_path)
            SEARCH_PATH = search_path.split(":");
        else
            SEARCH_PATH = null;
    }

    public static String rewriteFileName(String name) {
        if (REWRITE_FROM_DIR != null && REWRITE_TO_DIR != null) {
            if (name.contains(REWRITE_FROM_DIR)) {
                name = name.replaceFirst(REWRITE_FROM_DIR, REWRITE_TO_DIR);
            }
        }

        return name;
    }

    public static File getEdlFile(String name)
    {
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

    public static URL getEdlURL(String name) throws MalformedURLException {
        if (name == null) {
            throw new RuntimeException("An EDL resource is required");
        }

        if (name.startsWith("http:"))
            return new URL(name);

        if (name.startsWith("file:"))
            name = name.substring(5);

        File edl_file = getEdlFile(name);

        if (edl_file.exists())
        {
            return edl_file.toURI().toURL();
        }

        URL edl = null;

        /* Check that resource is defined as an EDL file. */
        if (!name.contains(".edl"))
        {
            /* The file extension should precede any macro following the resource name. */
            int idx = name.indexOf("&");

            if (-1 != idx)
                name = name.substring(0, idx) + ".edl" + name.substring(idx);
            else
                name += ".edl";
        }

        if (null != SEARCH_PATH)
        {
            for (String path : SEARCH_PATH)
            {
                edl = new URL(HTTP_DOC_ROOT + path + File.separator + name);
                LOGGER.log(Level.FINE, "Checking " + edl);
                try
                {
                    final HttpURLConnection edl_conn = (HttpURLConnection) edl.openConnection();
                    edl_conn.setRequestMethod("HEAD");
                    final int code = edl_conn.getResponseCode();
                    if (code == 200)
                    {
                        LOGGER.log(Level.FINE, "File found at " + edl);
                        return edl;
                    }
                }
                catch(Exception ex)
                {
                    LOGGER.log(Level.FINE, "File not found at " + path);
                }
            }
        }

        LOGGER.log(Level.INFO, "File (" + name + ") not found locally or at any specified remote locations.");

        return new URL("");
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
