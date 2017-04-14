package org.jlab.wedm.business.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jlab.wedm.persistence.io.ColorListParser;
import org.jlab.wedm.persistence.io.ScreenParser;
import org.jlab.wedm.persistence.model.HtmlScreen;
import org.jlab.wedm.persistence.model.ColorPalette;
import org.jlab.wedm.persistence.model.Macro;
import org.jlab.wedm.persistence.model.Screen;

/**
 *
 * @author ryans
 */
public class ScreenService {

    private static final Logger LOGGER = Logger.getLogger(ScreenService.class.getName());

    private ColorPalette colorList;
    private static final ConcurrentHashMap<String, HtmlScreen> SCREEN_CACHE
            = new ConcurrentHashMap<>();

    public ScreenService() throws FileNotFoundException {
        String colorfile = "colors.list";
        long start = System.currentTimeMillis();
        loadColorFile(colorfile);
        long end = System.currentTimeMillis();

        LOGGER.log(Level.FINEST, "Color List Load time: (seconds) {0}", (end - start) / 1000.0);
    }

    public HtmlScreen load(String name, List<Macro> macros) throws FileNotFoundException,
            IOException {

        ScreenParser parser = new ScreenParser();

        long start = System.currentTimeMillis();
        Screen parsedScreen = parser.parse(name, colorList, 0);
        long end = System.currentTimeMillis();

        LOGGER.log(Level.FINEST, "EDL Parse time: (seconds) {0}", (end - start) / 1000.0);

        start = System.currentTimeMillis();
        HtmlScreen screen = parsedScreen.toHtmlScreen();
        end = System.currentTimeMillis();

        screen = applyMacros(screen, macros);

        LOGGER.log(Level.FINEST, "HTML Render time: (seconds) {0}", (end - start) / 1000.0);

        /*HtmlScreen screen = SCREEN_CACHE.get(name);

        if (screen == null) {
            Screen parsedScreen = parser.parse(name, colorList, 0);
            screen = parsedScreen.toHtmlScreen();
            SCREEN_CACHE.put(name, screen);
        }*/
        return screen;
    }

    private void loadColorFile(String colorfile) throws FileNotFoundException {
        ColorListParser parser = new ColorListParser();

        colorList = parser.parse(colorfile);
    }

    private HtmlScreen applyMacros(HtmlScreen screen, List<Macro> macros) {
        String html = screen.getHtml();

        for (Macro m : macros) {
            html = html.replace(m.key, m.value);
        }

        return new HtmlScreen(screen.getCanonicalPath(), html, screen.getCss(), screen.getJs(),
                screen.getTitle());
    }
}
