package org.jlab.wedm.persistence.model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ryans
 */
public class ActiveSymbol extends ScreenProperties {

    public String file;
    public int numStates;
    public int[] minValues = new int[64];
    public int[] maxValues = new int[64];
    public List<String> controlPvs = new ArrayList<>();
    public boolean useOriginalSize;
    public boolean useOriginalColors;
    public Screen screen;

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

        String attrStr = getAttributesString(attributes);
        String classStr = getClassString(classes);

        String html = indent + "<div " + classStr + " " + attrStr;

        html = html + " style=\"";
        html = html + "width: " + w + "px; "
                + "height: " + h + "px; left: " + originX + "px; top: " + originY + "px;\"/>";

        if (screen != null && !screen.screenObjects.isEmpty()) {

            for (ScreenObject obj : screen.screenObjects) {

                Point childTranslation = new Point(-obj.x, -obj.y);

                html = html + obj.toHtml(indent + indentStep, indentStep, childTranslation);
            }
        }

        html = html + indent + "</div>\n";

        return html;
    }
}
