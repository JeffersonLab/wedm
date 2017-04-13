package org.jlab.wedm.widget;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ryans
 */
public class ActiveSymbol extends EmbeddedScreen {

    public int numStates;
    public int[] minValues = new int[64];
    public int[] maxValues = new int[64];
    public List<String> controlPvs = new ArrayList<>();
    public boolean useOriginalSize = false;
    public boolean useOriginalColors = false;

    @Override
    public String toHtml(String indent, String indentStep, Point translation) {

        int originX = x + translation.x;
        int originY = y + translation.y;

        attributes.put("id", "obj-" + objectId);

        classes.add("ActiveSymbol");
        classes.add("ScreenObject");

        if (numPvs == 1 && controlPvs.size() == 1) {
            attributes.put("data-pv", controlPvs.get(0));

            if (numStates > 0 && numStates <= 64) {
                String minStr = String.valueOf(minValues[0]);
                for (int i = 1; i < numStates; i++) {
                    minStr = minStr + " " + minValues[i];
                }
                attributes.put("data-min-values", minStr);

                String maxStr = String.valueOf(maxValues[0]);
                for (int i = 1; i < numStates; i++) {
                    maxStr = maxStr + " " + maxValues[i];
                }
                attributes.put("data-max-values", maxStr);
            }
        }

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

                Point childTranslation = new Point(-obj.x, -obj.y);

                if (!useOriginalSize) {
                    float xScale = (float) w / obj.w;
                    float yScale = (float) h / obj.h;

                    obj.styles.put("transform", "scale(" + xScale + ", " + yScale + ")");
                    obj.styles.put("transform-origin", "0 0");
                }

                if (!useOriginalColors) {
                    overrideColorsRecursive(obj);
                }

                html = html + obj.toHtml(indent + indentStep, indentStep, childTranslation);
            }
        }

        html = html + indent + "</div>\n";

        return html;
    }

    private void overrideColorsRecursive(ScreenObject obj) {
        obj.fgColor = fgColor;
        obj.lineColor = fgColor;
        
        obj.bgColor = bgColor;        
        obj.fillColor = bgColor;
        
        if (obj instanceof ActiveGroup) {
            List<ScreenObject> children = ((ActiveGroup) obj).children;

            for (ScreenObject child : children) {
                overrideColorsRecursive(child);
            }
        }
    }
}
