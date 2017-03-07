package org.jlab.wedm.persistence.model.html;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ryans
 */
public class TextScreenObject extends HtmlScreenObject {

    private static final Logger LOGGER = Logger.getLogger(TextScreenObject.class.getName());

    public String value = null;
    public String align;
    public int numLines = 1;
    protected Map<String, String> threeDStyles = new HashMap<>();
    protected Map<String, String> textStyles = new HashMap<>();

    protected void set3DStyles() {
        String className = this.getClass().getSimpleName();         
        if (!("ActiveButton".equals(className) || "ActiveMessageButton".equals(className) || "ActiveXTextDsp".equals(className)) || threeDimensional) {
            if (topShadowColor != null) {
                styles.put("border-top", "1px solid " + botShadowColor.toColorString());
                styles.put("border-left", "1px solid " + botShadowColor.toColorString());

                threeDStyles.put("border-top", "2px solid " + topShadowColor.toColorString());
                threeDStyles.put("border-left", "2px solid " + topShadowColor.toColorString());
            }

            if (botShadowColor != null) {
                styles.put("border-bottom", "1px solid " + topShadowColor.toColorString());
                styles.put("border-right", "1px solid " + topShadowColor.toColorString());

                threeDStyles.put("border-bottom", "2px solid " + botShadowColor.toColorString());
                threeDStyles.put("border-right", "2px solid " + botShadowColor.toColorString());
            }
        }        
    }
    
    @Override
    public String toHtml(String indent, String indentStep, Point translation) {
        
        set3DStyles();
        
        if (align != null) {
            textStyles.put("text-align", align);
        }

        if (border) {
            LOGGER.log(Level.INFO, "border being using in html text object!");

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
        
        String html = startHtml(indent, indentStep, translation);

        String threeDStyleStr = getStyleString(threeDStyles);
        String textStyleStr = getStyleString(textStyles);
        String indentPlusOne = indent + indentStep;
        String indentPlusTwo = indentPlusOne + indentStep;
        
        String val = org.apache.taglibs.standard.functions.Functions.escapeXml(value);
        
        if(val == null || "".equals(val)) {
            val = " ";
        }
        
        html = html + indentPlusOne + "<div class=\"text-wrap\" " + threeDStyleStr + ">\n";
        html = html + indentPlusTwo + "<div class=\"screen-text\" " + textStyleStr + ">";
        html = html + val  + "</div>\n";
        html = html + indentPlusOne + "</div>\n";
        html = html + endHtml(indent, indentStep);

        return html;
    }
}
