package org.jlab.wedm.widget;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import org.jlab.wedm.persistence.model.HtmlScreen;
import org.jlab.wedm.persistence.model.WEDMWidget;

/**
 *
 * @author ryans
 */
public class ActiveGroup extends CoreWidget {

    public List<WEDMWidget> children = new ArrayList<>();

    @Override
    public String toHtml(String indent, Point translation) {
        this.setCommonAttributes(); // Visibility, ID, and classes       

        int originX = x + translation.x;
        int originY = y + translation.y;

        styles.put("width", w + "px");
        styles.put("height", h + "px");
        styles.put("left", originX + "px");
        styles.put("top", originY + "px");
        
        String attrStr = this.getAttributesString(attributes);
        String classStr = this.getClassString(classes);
        String styleStr = this.getStyleString(styles);
        
        String html = indent + "<div " + attrStr + " " + classStr
                + " " + styleStr + ">\n";        
        
        Point childTranslation = new Point(-x, -y);

        if (!children.isEmpty()) {
            //html = html + indent + "<div style=\"position: relative;\">\n";
            for (WEDMWidget obj : children) {
                html = html + obj.toHtml(indent + HtmlScreen.INDENT_STEP + HtmlScreen.INDENT_STEP,
                        childTranslation);
            }
            //html = html + indent + "</div>\n";
        }

        html = html + indent + "</div>\n";

        return html;
    }
    
    public Dimension getDimension() {
        return new Dimension(w, h);
    }

    public Point getOrigin() {
        return new Point(x, y);
    }

    public void symbolScaleOverride(float xScale, float yScale) {
        styles.put("transform", "scale(" + xScale + ", " + yScale + ")");
        styles.put("transform-origin", "0 0");
    }    
}
