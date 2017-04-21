package org.jlab.wedm.widget;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jlab.wedm.persistence.io.TraitParser;
import org.jlab.wedm.persistence.model.ColorPalette;
import org.jlab.wedm.persistence.model.HtmlScreen;
import org.jlab.wedm.persistence.model.WEDMWidget;

/**
 *
 * @author ryans
 */
public class ActiveSymbol extends EmbeddedScreen {

    private static final Logger LOGGER = Logger.getLogger(ActiveSymbol.class.getName());

    public int numStates;
    public int[] minValues;
    public int[] maxValues;
    public String[] controlPvs;
    public boolean useOriginalSize = false;
    public boolean useOriginalColors = false;

    @Override
    public void parseTraits(Map<String, String> traits, ColorPalette palette) {
        super.parseTraits(traits, palette);

        numStates = TraitParser.parseInt(traits, "numStates", 0);
        minValues = TraitParser.parseIntArray(traits, numStates, "minValues");
        maxValues = TraitParser.parseIntArray(traits, numStates, "maxValues");

        controlPvs = TraitParser.parseStringArray(traits, 1, "controlPvs");
        
        useOriginalSize = TraitParser.parseBoolean(traits, "useOriginalSize");
        useOriginalColors = TraitParser.parseBoolean(traits, "useOriginalColors");
    }

    @Override
    public String toHtml(String indent, Point translation) {

        int originX = x + translation.x;
        int originY = y + translation.y;

        attributes.put("id", "obj-" + objectId);

        classes.add("ActiveSymbol");
        classes.add("ScreenObject");

        if (numPvs == 1 && controlPvs != null && controlPvs.length == 1) {
            attributes.put("data-pv", controlPvs[0]);

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

                if (obj instanceof ActiveGroup) {
                    ActiveGroup grp = (ActiveGroup) obj;

                    Point p = grp.getOrigin();
                    Dimension d = grp.getDimension();

                    Point childTranslation = new Point(-p.x, -p.y);

                    if (!useOriginalSize) {
                        float xScale = (float) w / d.width;
                        float yScale = (float) h / d.height;

                        grp.symbolScaleOverride(xScale, yScale);
                    }

                    if (!useOriginalColors) {
                        overrideColorsRecursive(obj);
                    }

                    html = html + obj.toHtml(indent + HtmlScreen.INDENT_STEP, childTranslation);
                } else {
                    LOGGER.log(Level.WARNING, "Symbol top level object is not an ActiveGroup: {0}",
                            obj.getClass().getSimpleName());
                }
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
