package org.jlab.wedm.widget;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.jlab.wedm.persistence.io.TraitParser;
import org.jlab.wedm.persistence.model.ColorPalette;
import org.jlab.wedm.persistence.model.HtmlScreen;
import org.jlab.wedm.persistence.model.Screen;
import org.jlab.wedm.persistence.model.WEDMWidget;

/**
 *
 * @author ryans
 */
public class ActivePictureInPicture extends EmbeddedScreen {

    protected String filePv = null;
    public List<Screen> screenList = new ArrayList<>();
    protected boolean noScroll = false;
    protected boolean center = false;
    protected String[] propagateMacros; // We currently always propagate no matter what...

    @Override
    public void parseTraits(Map<String, String> traits, ColorPalette palette) {
        super.parseTraits(traits, palette);

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

                Point childTranslation = new Point(0, 0); // We don't translate to top left like ActiveSymbol does

                String screenHtml = obj.toHtml(indent + HtmlScreen.INDENT_STEP, childTranslation);

                if (symbols != null && symbols[0] != null) {
                    screenHtml = applyMacros(screenHtml, symbols[0]);
                }

                html = html + screenHtml;
            }
        } else if (!screenList.isEmpty()) {
            for (int i = 0; i < screenList.size(); i++) {
                Screen s = screenList.get(i);
                String screenHtml = s.toHtmlBody(indent + HtmlScreen.INDENT_STEP);

                if (symbols != null && symbols[i] != null) {
                    screenHtml = applyMacros(screenHtml, symbols[i]);
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
            String key = "$(" + kv[0] + ")";
            String value = kv[1];
            html = html.replace(key, value);
        }

        return html;
    }
}
