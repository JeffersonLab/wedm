package org.jlab.wedm.persistence.io;

/**
 *
 * @author ryans
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
    
    public static String contextPrefix() {
        String contextPrefix = System.getenv("CONTEXT_PREFIX");
        
        if(contextPrefix == null) {
            contextPrefix = "";
        }
        
        return contextPrefix;
    }    
}
