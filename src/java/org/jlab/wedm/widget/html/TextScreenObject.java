package org.jlab.wedm.widget.html;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jlab.wedm.persistence.model.ColorPalette;
import org.jlab.wedm.persistence.model.HtmlScreen;
import org.jlab.wedm.widget.ScreenProperties;

/**
 *
 * @author slominskir
 */
public class TextScreenObject extends HtmlScreenObject {

    private static final Logger LOGGER = Logger.getLogger(TextScreenObject.class.getName());

    public String value = null;
    public String fontAlign;
    public int numLines = 1;
    protected Map<String, String> threeDStyles = new HashMap<>();
    protected Map<String, String> textStyles = new HashMap<>();

    @Override
    public void parseTraits(Map<String, String> traits, ScreenProperties properties) {
        super.parseTraits(traits, properties);
        
        value = traits.get("value");
        fontAlign = traits.get("fontAlign");
        
        // TODO: count how many lines?
    }
    
    protected void set3DStyles() {
        String className = this.getClass().getSimpleName();
        if (!("ActiveButton".equals(className) || "ActiveMessageButton".equals(className)
                || "ActiveXTextDsp".equals(className)) || threeDimensional) {
            if (topShadowColor != null && botShadowColor != null) {
                styles.put("border-top", "1px solid " + botShadowColor.toColorString());
                styles.put("border-left", "1px solid " + botShadowColor.toColorString());

                threeDStyles.put("border-top", "2px solid " + topShadowColor.toColorString());
                threeDStyles.put("border-left", "2px solid " + topShadowColor.toColorString());

                styles.put("border-bottom", "1px solid " + topShadowColor.toColorString());
                styles.put("border-right", "1px solid " + topShadowColor.toColorString());

                threeDStyles.put("border-bottom", "2px solid " + botShadowColor.toColorString());
                threeDStyles.put("border-right", "2px solid " + botShadowColor.toColorString());
            }
        }
    }

    @Override
    public String toHtml(String indent, Point translation) {
        
        set3DStyles();

        if (fontAlign != null) {
            textStyles.put("text-align", fontAlign);
        }

        if (useAlarmBorder) {
            styles.put("border", "2px solid transparent");
            // In EDM alarm border is drawn right on top of existing widget instead of making space for it like typical borders (part of width/height of obj).
            // We could have had an overlay to fake this, but instead we use negative margins and a border on the text wrap element.
            threeDStyles.put("margin", "-1px 0");
        } else if (border) {
            //LOGGER.log(Level.INFO, "border being using in html text object!");

            float px = 1;

            if (lineWidth != null) {
                px = lineWidth;
            }

            String style = "solid";

            String colorStr = "black";

            if (fgColor != null) {
                colorStr = fgColor.toColorString();
            }

            textStyles.put("border", px + "px " + style + " " + colorStr);
        }

        String html = startHtml(indent, translation);

        String threeDStyleStr = getStyleString(threeDStyles);
        String indentPlusOne = indent + HtmlScreen.INDENT_STEP;
        String indentPlusTwo = indentPlusOne + HtmlScreen.INDENT_STEP;

        html = html + indentPlusOne + "<div class=\"text-wrap\" " + threeDStyleStr + ">\n";
        html = getButtonFaceHtml(html, indentPlusTwo);
        html = html + indentPlusOne + "</div>\n";
        html = html + endHtml(indent);

        return html;
    }
    
    public String getButtonFaceHtml(String html, String indent) {
        String textStyleStr = getStyleString(textStyles);
        
        String val = org.apache.taglibs.standard.functions.Functions.escapeXml(value);

        if (val == null || "".equals(val)) {
            val = " ";
        }        
        
        html = html + indent + "<div class=\"screen-text\" " + textStyleStr + ">";
        html = html + val + "</div>\n";
        
        return html;
    }
}
