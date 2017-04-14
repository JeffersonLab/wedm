package org.jlab.wedm.widget;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import org.jlab.wedm.persistence.model.WEDMWidget;

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

            for (WEDMWidget obj : screen.screenObjects) {

                Point p = obj.getOrigin();
                Dimension d = obj.getDimension();
                
                Point childTranslation = new Point(-p.x, -p.y);

                if (!useOriginalSize) {
                    float xScale = (float) w / d.width;
                    float yScale = (float) h / d.height;

                    obj.symbolScaleOverride(xScale, yScale);
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

    private void overrideColorsRecursive(WEDMWidget obj) {
        obj.symbolColorOverride(bgColor, fgColor);
        
        if (obj instanceof ActiveGroup) {
            List<WEDMWidget> children = ((ActiveGroup) obj).children;

            for (WEDMWidget child : children) {
                overrideColorsRecursive(child);
            }
        }
    }
}
