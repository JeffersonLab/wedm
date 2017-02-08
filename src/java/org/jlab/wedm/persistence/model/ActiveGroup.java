package org.jlab.wedm.persistence.model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ryans
 */
public class ActiveGroup extends ScreenObject {

    public List<ScreenObject> children = new ArrayList<>();

    @Override
    public String toHtml(String indent, String indentStep, Point translation) {
        String html = indent + "<div class=\"ActiveGroup ScreenObject\" ";

        // html = html + "/>";
        int originX = x + translation.x;
        int originY = y + translation.y;

        html = html + "style=\"";
        html = html + "width: " + w + "px; "
                + "height: " + h + "px; left: " + originX + "px; top: " + originY + "px;\">\n";

        Point childTranslation = new Point(-x, -y);

        if (!children.isEmpty()) {
            //html = html + indent + "<div style=\"position: relative;\">\n";
            for (ScreenObject obj : children) {
                html = html + obj.toHtml(indent + indentStep + indentStep, indentStep,
                        childTranslation);
            }
            //html = html + indent + "</div>\n";
        }

        html = html + indent + "</div>\n";

        return html;
    }
}
