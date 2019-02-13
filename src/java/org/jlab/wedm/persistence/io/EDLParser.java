package org.jlab.wedm.persistence.io;

import java.io.File;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.jlab.wedm.persistence.model.EDLFont;

/**
 *
 * @author slominskir
 */
public class EDLParser {

    private static final Logger LOGGER = Logger.getLogger(EDLParser.class.getName());

    public static final String EDL_ROOT_DIR;
    public static final String REWRITE_FROM_DIR;
    public static final String REWRITE_TO_DIR;
    public static final EDLFont DEFAULT_FONT = new EDLFont("helvetica", false, false, 12);
    public static final String[] SEARCH_PATH;
    public static final String HTTP_DOC_ROOT;
    public static final String WEDM_DISABLE_CERTIFICATE_CHECK;

    /**
     * On Windows you could set EDL_DIR to a remote ExpanDrive mount say
     * E:\cs\opshome\edm then set REWRITE_FROM_DIR to / and REWRITE_TO_DIR to
     * E:\.
     */
    static {
        final String defaultRoot = "C:\\EDL";
        String root = System.getenv("EDL_DIR");
        if (root == null) {
            root = defaultRoot;
        }

        EDL_ROOT_DIR = root;

        REWRITE_FROM_DIR = System.getenv("REWRITE_FROM_DIR");
        REWRITE_TO_DIR = System.getenv("REWRITE_TO_DIR");

        WEDM_DISABLE_CERTIFICATE_CHECK = System.getenv("WEDM_DISABLE_CERTIFICATE_CHECK");

        // Use search path?
        String search_path = System.getenv("EDMDATAFILES");
        if (search_path != null) {
            SEARCH_PATH = search_path.split(":");
            LOGGER.log(Level.INFO, "EDMDATAFILES search path:\n{0}", Arrays.toString(SEARCH_PATH));
        } else {
            SEARCH_PATH = null;
        }

        // .. search path with http/https as starting point?
        HTTP_DOC_ROOT = System.getenv("EDMHTTPDOCROOT");
        if (HTTP_DOC_ROOT != null) {
            LOGGER.log(Level.INFO, "EDMHTTPDOCROOT: {0}", HTTP_DOC_ROOT);
            if (SEARCH_PATH == null) {
                LOGGER.log(Level.WARNING, "EDMHTTPDOCROOT must be used with EDMDATAFILES");
            }

            // If http:.. access is configured,
            // local files are often served with self-signed cert,
            // so disable validation if requested
            if (WEDM_DISABLE_CERTIFICATE_CHECK != null) {
                try {
                    trustAnybody();
                } catch (Exception ex) {
                    LOGGER.log(Level.WARNING, "Cannot disable certificate checks", ex);
                }
            }
        }
    }

    /**
     * Allow https:// access to self-signed certificates
     *
     * @throws Exception on error
     */
    private static synchronized void trustAnybody() throws Exception {
        // Create a trust manager that does not validate certificate chains.
        final TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    /* NOP */ }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    /* NOP */ }
            }
        };
        final SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        // All-trusting host name verifier
        final HostnameVerifier allHostsValid = (hostname, session) -> true;
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

        LOGGER.log(Level.INFO, "Disabled https certificate checks");
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

    /**
     * Resolve name to URL
     *
     * @param name Name may be a http:, https:, file: URL, but also just a name
     * that refers to a local file, or that is found on the EDMDATAFILES search
     * path.
     * @return Resolved URL for the name or <code>null</code>
     * @throws MalformedURLException
     */
    public static URL getEdlURL(String name) throws MalformedURLException {
        Objects.requireNonNull(name, "An EDL resource is required");

        // Use complete http.. URL as is
        if (name.startsWith("http:") || name.startsWith("https:")) {
            LOGGER.log(Level.FINE, "Using {0} as provided", name);
            return new URL(name);
        }

        final File edl_file;
        // For file URL, get the file.
        // Otherwise turn name into absolute filename within EDL_ROOT_DIR
        if (name.startsWith("file:")) {
            edl_file = new File(URI.create(name));
        } else {
            edl_file = getEdlFile(name);
        }

        // Done?
        if (edl_file.exists()) {
            LOGGER.log(Level.FINE, "Found local file {0}", edl_file);
            return edl_file.toURI().toURL();
        }

        // Try search path
        URL edl = null;

        // Assert that name has *.edl ending
        if (!name.contains(".edl")) {
            // The file extension should precede any macro following the resource name
            final int idx = name.indexOf("&");
            if (idx != -1) {
                name = name.substring(0, idx) + ".edl" + name.substring(idx);
            } else {
                name += ".edl";
            }
        }

        if (SEARCH_PATH != null) {
            for (String path : SEARCH_PATH) {
                if (path.startsWith("/")) {
                    edl = new URL(HTTP_DOC_ROOT + path.substring(1) + "/" + name);
                } else {
                    edl = new URL(HTTP_DOC_ROOT + path + "/" + name);
                }
                LOGGER.log(Level.FINER, "Checking {0}", edl);
                try {
                    // Perform HEAD request to check for presence
                    final HttpURLConnection edl_conn = (HttpURLConnection) edl.openConnection();
                    edl_conn.setRequestMethod("HEAD");
                    final int code = edl_conn.getResponseCode();
                    if (code == 200) {
                        LOGGER.log(Level.FINE, "File found at {0}", edl);
                        return edl;
                    }
                } catch (Exception ex) {
                    LOGGER.log(Level.FINER, "File not found at " + path, ex);
                }
            }
        }

        LOGGER.log(Level.INFO, "File ({0}) not found locally nor at any specified remote locations.", name);
        return null;
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
}
