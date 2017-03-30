package org.jlab.wedm.persistence.model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ryans
 */
public class ActivePictureInPicture extends EmbeddedScreen {

    public String filePv = null;
    public List<Screen> screenList = new ArrayList<>();

    @Override
    public String toHtml(String indent, String indentStep, Point translation) {

        int originX = x + translation.x;
        int originY = y + translation.y;

        attributes.put("id", "obj-" + objectId);
        
        if(filePv != null) {
            attributes.put("data-pv", filePv);
        }
        
        classes.add("ActivePictureInPicture");
        classes.add("ScreenObject");

        styles.put("width", w + "px");
        styles.put("height", h + "px");
        styles.put("left", originX + "px");
        styles.put("top", originY + "px");

        String attrStr = getAttributesString(attributes);
        String classStr = getClassString(classes);
        String styleStr = getStyleString(styles);

        String html = indent + "<div " + classStr + " " + attrStr + " " + styleStr + "/>";

        if (screen != null && !screen.screenObjects.isEmpty()) {

            for (ScreenObject obj : screen.screenObjects) {

                Point childTranslation = new Point(0, 0); // We don't translate to top left like ActiveSymbol does

                html = html + obj.toHtml(indent + indentStep, indentStep, childTranslation);
            }
        } else if (!screenList.isEmpty()) {
            for (Screen s : screenList) {
                s.indent = indent + indentStep;
                html = html + s.toHtmlBody();
            }
        }

        html = html + indent + "</div>\n";

        return html;
    }
}
