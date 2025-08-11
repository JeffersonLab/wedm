package org.jlab.wedm.persistence.io;

import jakarta.json.Json;
import jakarta.json.stream.JsonParser;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.*;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
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
  public static final boolean EDMRELATIVEPATHS;
  public static final boolean WEDM_DISABLE_RELATIVEPATHS_CHECK;

  public static final String OTF_DIR;

  public static final String OTF_MAP_FILENAME = "edl.json";
  public static final Map<String, String> OTF_MAP = new HashMap<>();

  /**
   * On Windows you could set EDL_DIR to a remote ExpanDrive mount say E:\cs\opshome\edm then set
   * REWRITE_FROM_DIR to / and REWRITE_TO_DIR to E:\.
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

    // EDM Version 1-12-105J, ca. June 2021, supports this environment variable
    // to enable relative path support.
    EDMRELATIVEPATHS = "yes".equals(System.getenv("EDMRELATIVEPATHS"));
    LOGGER.log(Level.INFO, "EDMRELATIVEPATHS=" + (EDMRELATIVEPATHS ? "yes" : "no"));

    WEDM_DISABLE_RELATIVEPATHS_CHECK =
        "yes".equals(System.getenv("WEDM_DISABLE_RELATIVEPATHS_CHECK"));
    LOGGER.log(
        Level.INFO,
        "WEDM_DISABLE_RELATIVEPATHS_CHECK=" + (WEDM_DISABLE_RELATIVEPATHS_CHECK ? "yes" : "no"));

    OTF_DIR = System.getenv("OTF_DIR");

    if (OTF_DIR != null) {
      loadOtfMap();
    }
  }

  private static void loadOtfMap() {
    String path = OTF_DIR + File.separator + OTF_MAP_FILENAME;

    try (FileInputStream fis = new FileInputStream(path);
        JsonParser parser = Json.createParser(fis); ) {

      String cmd = null;
      String edl = null;

      while (parser.hasNext()) {
        JsonParser.Event e = parser.next();

        // System.err.println("Event: "  + e);

        if (e == JsonParser.Event.KEY_NAME) {
          switch (parser.getString()) {
            case "cmd":
              parser.next();
              cmd = parser.getString();
              // System.out.println("cmd: " + cmd);
              break;
            case "edl":
              parser.next();
              edl = parser.getString();
              // System.out.println("edl: " + edl);
          }
        } else if (e == JsonParser.Event.END_OBJECT) {
          if (cmd != null && edl != null) {
            EDLParser.OTF_MAP.put(cmd, edl);
          }
        }
      }
    } catch (IOException e) {
      LOGGER.log(Level.WARNING, "Unable to parse file: " + path);
      e.printStackTrace();
    }
  }

  /**
   * Allow https:// access to self-signed certificates
   *
   * @throws Exception on error
   */
  private static synchronized void trustAnybody() throws Exception {
    // Create a trust manager that does not validate certificate chains.
    final TrustManager[] trustAllCerts =
        new TrustManager[] {
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
   * Resolve *.edl name to URL
   *
   * @param name Name may be a http:, https:, file: URL, but also just a name that refers to a local
   *     file, or that is found on the EDMDATAFILES search path.
   * @return Resolved URL for the name or <code>null</code>
   * @throws MalformedURLException If the URL is malformed
   * @throws URISyntaxException If the URI syntax is invalid
   */
  public static URL getEdlURL(String name) throws MalformedURLException, URISyntaxException {
    return getURL(name, true);
  }

  /**
   * Resolve name to URL
   *
   * @param name Name may be a http:, https:, file: URL, but also just a name that refers to a local
   *     file, or that is found on the EDMDATAFILES search path.
   * @param force_edl Add *.edl suffix if not already in name?
   * @return Resolved URL for the name or <code>null</code>
   * @throws MalformedURLException If the URL is malformed
   * @throws URISyntaxException If the URI syntax is invalid
   */
  public static URL getURL(String name, final boolean force_edl)
      throws MalformedURLException, URISyntaxException {
    return getURL(null, name, force_edl);
  }

  /**
   * Combine parent URL and name into URL relative to parent
   *
   * <p>Check access based on WEDM_DISABLE_RELATIVEPATHS_CHECK
   *
   * @param parent Parent URL, for example "http://some/path/file.edl", or <code>null</code>
   * @param name Name, for example "sub/another.edl"
   * @return URL for name relative to parent, for example "http://some/path/sub/another.edl". <code>
   *     null</code> if EDMRELATIVEPATHS is not enabled, or check of relative path is enabled and
   *     fails
   */
  public static URL getRelativeURL(final URL parent, String name) {
    if (!EDMRELATIVEPATHS) return null;

    if (parent == null) return null;

    try {
      // Locate separator between 'folder' and 'file'
      final URI parent_uri = parent.toURI();
      String path = parent_uri.getPath();
      int sep = path.lastIndexOf('/');
      if (sep >= 0) path = path.substring(0, sep);

      // Construct URI where 'file' in parent is replaced by 'name'
      final URI relative_uri =
          new URI(
              parent_uri.getScheme(),
              parent_uri.getUserInfo(),
              parent_uri.getHost(),
              parent_uri.getPort(),
              path + "/" + name,
              parent_uri.getQuery(),
              parent_uri.getFragment());
      final URL url = relative_uri.toURL();
      if (testAccess(url)) return url;
    } catch (Exception ex) {
      LOGGER.log(
          Level.FINER, "Cannot check relative path for parent " + parent + " and " + name, ex);
    }
    return null;
  }

  /**
   * @param url URL for which read access will be tested
   * @return <code>true</code> if url can be read or test is disabled
   */
  private static boolean testAccess(final URL url) {
    if (WEDM_DISABLE_RELATIVEPATHS_CHECK) return true;

    try {
      url.openStream().close();
      return true;
    } catch (Exception ex) {
      // Ignore
    }

    return false;
  }

  /**
   * Resolve name to URL
   *
   * @param parent Parent display URL or <code>null</code>.
   * @param name Name may be a http:, https:, file: URL, but also just a name that refers to a local
   *     file, or that is found on the EDMDATAFILES search path.
   * @param force_edl Add *.edl suffix if not already in name?
   * @return Resolved URL for the name or <code>null</code>
   * @throws MalformedURLException
   */
  public static URL getURL(URL parent, String name, final boolean force_edl)
      throws MalformedURLException, URISyntaxException {
    Objects.requireNonNull(name, "An EDL resource is required");

    // Assert that name has *.edl ending
    if (force_edl && !name.contains(".edl")) {
      // The file extension should precede any macro following the resource name
      final int idx = name.indexOf("&");
      if (idx != -1) {
        name = name.substring(0, idx) + ".edl" + name.substring(idx);
      } else {
        name += ".edl";
      }
    }

    // Check for relative path
    final URL relative = getRelativeURL(parent, name);
    if (relative != null) return relative;

    // Use complete http.. URL as is
    if (name.startsWith("http:") || name.startsWith("https:")) {
      // .. except when files are hosted at a HTTP_DOC_ROOT,
      // in which case all complete URLs must start there
      if (HTTP_DOC_ROOT != null && !name.startsWith(HTTP_DOC_ROOT)) {
        LOGGER.log(Level.WARNING, "Rejecting URL {0}", name);
        return null;
      }
      LOGGER.log(Level.FINE, "Using {0} as provided", name);
      return new URI(name).toURL();
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

    if (SEARCH_PATH != null) {
      for (String path : SEARCH_PATH) {
        if (path.startsWith("/")) {
          edl = new URI(HTTP_DOC_ROOT + path.substring(1) + "/" + name).toURL();
        } else {
          edl = new URI(HTTP_DOC_ROOT + path + "/" + name).toURL();
        }
        LOGGER.log(Level.FINER, "Checking {0}", edl);
        try {
          // Perform HEAD request to check for presence
          final HttpURLConnection edl_conn = (HttpURLConnection) edl.openConnection();
          int code;
          try {
            edl_conn.setRequestMethod("HEAD");
            edl_conn
                .connect(); // connect() is automatically triggered by getResponseCode(), but let's
            // make it explicit
            code = edl_conn.getResponseCode();
          } finally {
            edl_conn.disconnect(); // Don't hold any resources, we won't use this instance of
            // HttpURLConnection again
          }
          if (code == 200) {
            LOGGER.log(Level.FINE, "File found at {0}", edl);
            return edl;
          }
        } catch (Exception ex) {
          LOGGER.log(Level.FINER, "File not found at " + path, ex);
        }
      }
    }

    LOGGER.log(
        Level.INFO, "File ({0}) not found locally nor at any specified remote locations.", name);
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
