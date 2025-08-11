package org.jlab.wedm.persistence.io;

/**
 * @author slominskir
 */
public class Functions {
  public static String escapeFileName(String name) {
    name = name.replace(":", "_");

    // Below is the likely incomplete list of characters that are generally bad in filenames.
    // We only need colon right now due to activeXTextDspClass:noedit.
    // Mapping all of them to underscore creates an opportunity for name conflict, but this is
    // unlikely
    /*name = name.replace("~", "_");
    name = name.replace("#", "_");
    name = name.replace("%", "_");
    name = name.replace("&", "_");
    name = name.replace("{", "_");
    name = name.replace("}", "_");
    name = name.replace("\\", "_");
    name = name.replace(":", "_");
    name = name.replace("<", "_");
    name = name.replace(">", "_");
    name = name.replace("?", "_");
    name = name.replace("/", "_");
    name = name.replace("+", "_");
    name = name.replace("|", "_");
    name = name.replace("\"", "_");*/

    return name;
  }

  public static String epics2webHost() {
    String host = System.getenv("EPICS_2_WEB_HOST");

    if (host == null) {
      host = "";
    }

    return host;
  }

  public static String epics2webPrefix() {
    String prefix = epics2webHost();

    if (prefix == null || prefix.equals("")) {
      prefix = "";
    } else {
      prefix = "//" + prefix; // Create protocol-relative URL
    }

    String contextPrefix = contextPrefix();

    if (contextPrefix != null) {
      prefix = prefix + contextPrefix;
    }

    return prefix;
  }

  public static String contextPrefix() {
    String contextPrefix = System.getenv("CONTEXT_PREFIX");

    if (contextPrefix == null) {
      contextPrefix = "";
    }

    return contextPrefix;
  }
}
