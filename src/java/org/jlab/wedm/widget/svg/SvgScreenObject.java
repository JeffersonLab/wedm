package org.jlab.wedm.widget.svg;

import java.awt.Point;
import org.jlab.wedm.persistence.model.HtmlScreen;
import org.jlab.wedm.widget.CoreWidget;

/**
 *
 * @author slominskir
 */
public abstract class SvgScreenObject extends CoreWidget {
    public static final String DASH_SPACING = "4, 4";     
    
    public String startSvg(String indent, Point translation) {
        
        // EDM draws a line even if object has 0 width and 0 line thickness.  This is madness.
        // Further, the box model is likely different too:
        // WEDM uses BORDER-BOX style for HTML elements (border included in dimensions)
        // and for SVG elements border: is placed in middle of dimensions (half of it is inside, half outside).
        // EDM seems to be widget-specific 
        // For HTML we could use CONTENT-BOX (border is drawn outside dimensions completely).
        // For SVG we could try to control box model / stroke location to be like CONTENT-BOX or BORDER-BOX, but it isn't easy:
        // https://stackoverflow.com/questions/7241393/can-you-control-how-an-svgs-stroke-width-is-drawn
        
        // EDM Oddity
        if (lineWidth != null) {
            if (lineWidth < 1.0) {
                lineWidth = 1.0f; // This is due to EDM rendering 0 width border as 1+
            }
        }     
        
        // EDM Oddity
        if(w == 0) {
            w = 1; // This is due to EDM rendering 0 width rect with 0 size border as 1
        }
        if(h == 0) {
            h = 1;
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

    public String endSvg(String indent) {
        return indent + "</svg>\n";
    }    
    
    public String toSvg(String indent, Point translation) {
        return "";
    }
    
    @Override
    public String toHtml(String indent, Point translation) {
        String html;
        
        Point childTranslation = new Point(-x, -y);
        
        html = startSvg(indent, translation);
        html = html + toSvg(indent + HtmlScreen.INDENT_STEP, childTranslation);
        html = html + endSvg(indent);

        return html;
    } 
}
