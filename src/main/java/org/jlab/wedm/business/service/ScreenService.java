package org.jlab.wedm.business.service;

import static org.jlab.wedm.persistence.io.ColorListParser.COLOR_FILE_PATH;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jlab.wedm.persistence.io.ColorListParser;
import org.jlab.wedm.persistence.io.EDLParser;
import org.jlab.wedm.persistence.io.ScreenParser;
import org.jlab.wedm.persistence.model.ColorPalette;
import org.jlab.wedm.persistence.model.HtmlScreen;
import org.jlab.wedm.persistence.model.Macro;
import org.jlab.wedm.persistence.model.Screen;

/**
 * @author slominskir
 */
public class ScreenService {

  private static final Logger LOGGER = Logger.getLogger(ScreenService.class.getName());

  private ColorPalette colorList;
  public static final ConcurrentHashMap<String, HtmlScreen> SCREEN_CACHE =
      new ConcurrentHashMap<>();

  private static final boolean CACHE_SCREENS_ENABLED = true;

  public ScreenService() throws FileNotFoundException {
    long start = System.currentTimeMillis();
    loadColorFile();
    long end = System.currentTimeMillis();

    LOGGER.log(Level.FINEST, "Color List Load time: (seconds) {0}", (end - start) / 1000.0);
  }

  public HtmlScreen load(String name, List<Macro> macros) throws IOException, URISyntaxException {

    // Resolve name into URL
    final URL url = EDLParser.getEdlURL(name);
    if (url == null) {
      return null;
    }

    // Already in cache?
    String cache_key = url.toString();
    HtmlScreen screen = SCREEN_CACHE.get(cache_key);
    // .. and not expired?
    if (screen != null) {
      URLConnection con = url.openConnection();
      long lastModified;
      if (con instanceof HttpURLConnection) {
        HttpURLConnection httpCon = (HttpURLConnection) con;
        try {
          httpCon.setRequestMethod("HEAD"); // No need for full GET
          httpCon.connect(); // Send request
          lastModified = httpCon.getLastModified();
        } finally {
          httpCon.disconnect(); // Free up HttpURLConnection instance resources (this particular
          // instance isn't making another request)
        }
      } else {
        lastModified = con.getLastModified(); // local file
      }

      if (lastModified > screen.getModifiedDate()) {
        LOGGER.log(Level.WARNING, "File changed so flushing cache: {0}", cache_key);
        SCREEN_CACHE.remove(cache_key);
        screen = null;
      }
    }

    if (screen == null) {
      ScreenParser parser = new ScreenParser();

      long start = System.currentTimeMillis();
      Screen parsedScreen = parser.parse(url, colorList, 0);
      // long end = System.currentTimeMillis();

      // LOGGER.log(Level.FINEST, "EDL Parse time: (seconds) {0}", (end - start) / 1000.0);
      // start = System.currentTimeMillis();
      screen = parsedScreen.toHtmlScreen();
      long end = System.currentTimeMillis();

      float generateSeconds = (end - start) / 1000.0f;

      // LOGGER.log(Level.FINEST, "Generate time: (seconds) {0}", generateSeconds);
      screen.setGenerateSeconds(generateSeconds);

      if (CACHE_SCREENS_ENABLED) {
        SCREEN_CACHE.put(cache_key, screen);
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

    // First we replace cached screen macros placeholder with value
    StringBuilder builder = new StringBuilder();
    if (!macros.isEmpty()) {
      Macro m = macros.get(0);
      builder.append(m.key.substring(2, m.key.length() - 1));
      builder.append("=");
      builder.append(m.value);

      for (int i = 1; i < macros.size(); i++) {
        m = macros.get(i);
        builder.append(",");
        builder.append(m.key.substring(2, m.key.length() - 1));
        builder.append("=");
        builder.append(m.value);
      }
    }

    String macroString = builder.toString();
    html = html.replace(Screen.ROOT_SCREEN_MACRO_PLACEHOLDER, macroString);

    // Now we actually apply macros to screen
    for (Macro m : macros) {

      /*Avoid cross-site scripting and malformed HTML by escaping, but sacrifice ability to use XML reserved characters in Macros ("'&<>)*/
      String v = org.apache.taglibs.standard.functions.Functions.escapeXml(m.value);

      html = html.replace(m.key, v);
    }

    return new HtmlScreen(
        screen.getCanonicalPath(),
        screen.getModifiedDate(),
        html,
        screen.getCss(),
        screen.getJs(),
        screen.getTitle());
  }
}
