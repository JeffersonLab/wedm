package org.jlab.wedm.widget.html;

import java.awt.Point;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jlab.wedm.widget.ScreenObject;

/**
 *
 * @author ryans
 */
public class HtmlScreenObject extends ScreenObject {

    private static final Logger LOGGER = Logger.getLogger(HtmlScreenObject.class.getName());    
    
    public boolean threeDimensional = false; // might only apply to ActiveButton?

    public String startHtml(String indent, String indentStep, Point translation) {
        this.setCommonAttributes();

        if (bgColor != null && !useDisplayBg) {
            styles.put("background-color", bgColor.toColorString());
        } 
        
        if (lineColor != null) {
            float px = 1;

            if (lineWidth != null) {
                px = lineWidth;
            }

            String style = "solid";

            if (dash) {
                style = "dotted";
            }

            styles.put("border", px + "px " + style + " " + lineColor.toColorString());
        }             
        
        if (fill && fillColor != null && bgColor == null) {
            LOGGER.log(Level.INFO, "fill being using in HTML object!");
            styles.put("background-color", fillColor.toColorString());
        }                      
        
        int originX = x + translation.x;
        int originY = y + translation.y;

        styles.put("left", originX + "px");
        styles.put("top", originY + "px");      
        
        String attrStr = this.getAttributesString(attributes);
        String classStr = this.getClassString(classes);
        String styleStr = this.getStyleString(styles);
        
        String html = indent + "<div " + attrStr + " " + classStr
                + " " + styleStr + ">\n";

        return html;
    }

    public String endHtml(String indent, String indentStep) {
        return indent + "</div>\n";
    }

    @Override
    public String toHtml(String indent, String indentStep, Point translation) {
        String html;

        html = startHtml(indent, indentStep, translation);
        html = html + endHtml(indent, indentStep);

        return html;
    }
}
