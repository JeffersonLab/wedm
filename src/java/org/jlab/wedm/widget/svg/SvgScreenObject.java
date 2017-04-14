package org.jlab.wedm.widget.svg;

import java.awt.Point;
import org.jlab.wedm.widget.CoreWidget;

/**
 *
 * @author ryans
 */
public abstract class SvgScreenObject extends CoreWidget {
    public static final String DASH_SPACING = "4, 4";     
    
    public String startSvg(String indent, String indentStep, Point translation) {
        
        // EDM Oddity
        if (lineWidth != null) {
            if (lineWidth < 1.0) {
                lineWidth = 1.0f; // This is due to EDM rendering 0 width border as 1+
            }
        }     
        
        this.setCommonAttributes();       

        int originX = x + translation.x;
        int originY = y + translation.y;
        
        styles.put("left", originX + "px");
        styles.put("top", originY + "px");      

        String attrStr = this.getAttributesString(attributes);
        String classStr = this.getClassString(classes);
        String styleStr = this.getStyleString(styles);         
        
        String html = indent + "<svg " + attrStr + " " + classStr
                + " " + styleStr + ">\n";        
        
        return html;
    }

    public String endSvg(String indent, String indentStep) {
        return indent + "</svg>\n";
    }    
    
    public String toSvg(String indent, String indentStep, Point translation) {
        return "";
    }
    
    @Override
    public String toHtml(String indent, String indentStep, Point translation) {
        String html;
        
        Point childTranslation = new Point(-x, -y);
        
        html = startSvg(indent, indentStep, translation);
        html = html + toSvg(indent + indentStep, indentStep, childTranslation);
        html = html + endSvg(indent, indentStep);

        return html;
    } 
}
