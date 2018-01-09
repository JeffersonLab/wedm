package org.jlab.wedm.business.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jlab.wedm.persistence.io.ColorListParser;
import static org.jlab.wedm.persistence.io.ColorListParser.COLOR_FILE_PATH;
import org.jlab.wedm.persistence.io.EDLParser;
import org.jlab.wedm.persistence.io.ScreenParser;
import org.jlab.wedm.persistence.model.HtmlScreen;
import org.jlab.wedm.persistence.model.ColorPalette;
import org.jlab.wedm.persistence.model.Macro;
import org.jlab.wedm.persistence.model.Screen;

/**
 *
 * @author slominskir
 */
public class ScreenService {
    
    private static final Logger LOGGER = Logger.getLogger(ScreenService.class.getName());
    
    private ColorPalette colorList;
    public static final ConcurrentHashMap<String, HtmlScreen> SCREEN_CACHE
            = new ConcurrentHashMap<>();
    
    private static final boolean CACHE_SCREENS_ENABLED = true;
    
    /*static {
        if (CACHE_SCREENS_ENABLED) {
            FileChangeWatcher watcher = new FileChangeWatcher();
            Thread t = new Thread(watcher);
            t.setDaemon(true);
            t.start();
        }
    }*/
    
    public ScreenService() throws FileNotFoundException {
        long start = System.currentTimeMillis();
        loadColorFile();
        long end = System.currentTimeMillis();
        
        LOGGER.log(Level.FINEST, "Color List Load time: (seconds) {0}", (end - start) / 1000.0);
    }
    
    public HtmlScreen load(String name, List<Macro> macros) throws FileNotFoundException,
            IOException {
        
        File f = EDLParser.getEdlFile(name);
        String canonicalPath = f.getCanonicalPath();
        
        HtmlScreen screen = SCREEN_CACHE.get(canonicalPath);
        
        if(screen != null) {
            if(f.lastModified() > screen.getModifiedDate()) {
                LOGGER.log(Level.WARNING, "File changed so flushing cache: {0}", name);
                SCREEN_CACHE.remove(name);
                screen = null;
            }
        }
        
        if (screen == null) {
            ScreenParser parser = new ScreenParser();
            
            long start = System.currentTimeMillis();
            Screen parsedScreen = parser.parse(canonicalPath, colorList, 0);
            //long end = System.currentTimeMillis();
            
            //LOGGER.log(Level.FINEST, "EDL Parse time: (seconds) {0}", (end - start) / 1000.0);
            
            //start = System.currentTimeMillis();
            screen = parsedScreen.toHtmlScreen();
            long end = System.currentTimeMillis();
            
            float generateSeconds = (end - start) / 1000.0f;
            
            //LOGGER.log(Level.FINEST, "Generate time: (seconds) {0}", generateSeconds);
            
            screen.setGenerateSeconds(generateSeconds);
            
            if (CACHE_SCREENS_ENABLED) {
                SCREEN_CACHE.put(canonicalPath, screen);
            }
        }
        
        screen.incrementUsageCount();
        
        screen = applyMacros(screen, macros);
        
        return screen;
    }
    
    private void loadColorFile() throws FileNotFoundException {        
        ColorListParser parser = new ColorListParser();
        
        File file = new File(COLOR_FILE_PATH);        
        
        colorList = parser.parse(file);
    }
    
    private HtmlScreen applyMacros(HtmlScreen screen, List<Macro> macros) {
        String html = screen.getHtml();
        
        for (Macro m : macros) {
            
            /*Avoid cross-site scripting and malformed HTML by escaping, but sacrifice ability to use XML reserved characters in Macros ("'&<>)*/
            String v = org.apache.taglibs.standard.functions.Functions.escapeXml(m.value);
            
            html = html.replace(m.key, v);
        }
        
        return new HtmlScreen(screen.getCanonicalPath(), screen.getModifiedDate(), html, screen.getCss(), screen.getJs(),
                screen.getTitle());
    }
}
