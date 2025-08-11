package org.jlab.wedm.widget;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jlab.wedm.persistence.io.TraitParser;
import org.jlab.wedm.persistence.model.HtmlScreen;
import org.jlab.wedm.persistence.model.Screen;
import org.jlab.wedm.persistence.model.WEDMWidget;

/**
 * @author slominskir
 */
public class ActivePictureInPicture extends EmbeddedScreen {

  private static final Logger LOGGER = Logger.getLogger(ActivePictureInPicture.class.getName());

  protected String filePv = null;
  public List<Screen> screenList = new ArrayList<>();
  protected boolean noScroll = false;
  protected boolean center = false;
  protected String[] propagateMacros; // We currently always propagate no matter what...

  @Override
  public void parseTraits(Map<String, String> traits, ScreenProperties properties) {
    super.parseTraits(traits, properties);

    // Strings
    filePv = traits.get("filePv");

    // Booleans
    noScroll = TraitParser.parseBoolean(traits, "noScroll");
    center = TraitParser.parseBoolean(traits, "center");
  }

  @Override
  public String toHtml(String indent, Point translation) {

    int originX = x + translation.x;
    int originY = y + translation.y;

    attributes.put("id", "obj-" + objectId);

    if (filePv != null) {
      attributes.put("data-pv", filePv);
    }

    if (noScroll) {
      classes.add("noscroll");
    }

    if (center) {
      classes.add("pip-center");
    }

    classes.add("ActivePictureInPicture");
    classes.add("ScreenObject");

    styles.put("width", w + "px");
    styles.put("height", h + "px");
    styles.put("left", originX + "px");
    styles.put("top", originY + "px");
    styles.put("pointer-events", "auto"); // Without this scroll bars don't work

    if (topShadowColor != null && botShadowColor != null) {
      styles.put("border-top", "2px solid " + botShadowColor.toColorString());
      styles.put("border-left", "2px solid " + botShadowColor.toColorString());

      styles.put("border-bottom", "2px solid " + topShadowColor.toColorString());
      styles.put("border-right", "2px solid " + topShadowColor.toColorString());
    }

    String attrStr = getAttributesString(attributes);
    String classStr = getClassString(classes);
    String styleStr = getStyleString(styles);

    String html = indent + "<div " + classStr + " " + attrStr + " " + styleStr + ">\n";

    if (screen != null && !screen.screenObjects.isEmpty()) {

      for (WEDMWidget obj : screen.screenObjects) {

        Point childTranslation =
            new Point(0, 0); // We don't translate to top left like ActiveSymbol does

        String screenHtml = obj.toHtml(indent + HtmlScreen.INDENT_STEP, childTranslation);

        if (symbols != null && symbols[0] != null) {
          screenHtml = applyMacros(screenHtml, symbols[0]);
        }

        html = html + screenHtml;
      }
    } else if (!screenList.isEmpty()) {
      for (int i = 0; i < screenList.size(); i++) {
        Screen s = screenList.get(i);

        String macros = null;

        if (symbols != null && symbols[i] != null) {
          macros = symbols[i];
        }

        String screenHtml = s.toHtmlBody(indent + HtmlScreen.INDENT_STEP, macros);

        if (macros != null) {
          screenHtml = applyMacros(screenHtml, macros);
        }

        html = html + screenHtml;
      }
    }

    html = html + indent + "</div>\n";

    return html;
  }

  private String applyMacros(String html, String macroString) {
    String[] macros = macroString.split(",");
    for (String m : macros) {
      String[] kv = m.split("=");
      if (kv.length == 2) {
        String key = "$(" + kv[0].trim() + ")";
        String value = kv[1].trim();

        // System.out.println("key: '" + key + "'");
        // System.out.println("value: '" + value + "'");
        html = html.replace(key, value);
      } else {
        LOGGER.log(Level.INFO, "Skipping malformed macro: {0}", m);
      }
    }

    return html;
  }
}
