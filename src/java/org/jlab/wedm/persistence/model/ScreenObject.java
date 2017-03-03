package org.jlab.wedm.persistence.model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ryans
 */
public class ScreenObject {

    public int objectId;
    public int x;
    public int y;
    public int w;
    public int h;
    public int numPvs;
    public Float visMin = null;
    public Float visMax = null;
    public Float lineWidth = null;
    public Float max = null;
    public Float min = null;
    public Float origin = null;
    public EDLColor bgColor;
    public EDLColor fgColor;
    public EDLColor topShadowColor;
    public EDLColor botShadowColor;
    public EDLColor lineColor;
    public EDLColor fillColor;
    public EDLColor onColor;
    public EDLColor offColor;
    public EDLColor indicatorColor;
    public boolean useDisplayBg = false;
    public boolean invisible = false;
    public boolean fill = false;
    public boolean dash = false;
    public boolean visInvert = false;
    public boolean autoSize = false;
    public boolean decimal = false;
    public boolean border = false;
    public boolean limitsFromDb = false;
    public boolean indicatorAlarm = false;
    public boolean lineAlarm = false;
    public boolean fillAlarm = false;
    public boolean fgAlarm = false;
    public boolean bgAlarm = false;
    public Boolean horizontal = null;    //ChoiceButton default = false, BarMeter default = true.
    public EDLFont font;
    public String controlPv;
    public String visPv;
    public String alarmPv;
    public String indicatorPv;

    public Map<String, String> attributes = new HashMap<>();
    public Map<String, String> styles = new HashMap<>();
    public List<String> classes = new ArrayList<>();

    protected void setCommonAttributes() {
        String className = this.getClass().getSimpleName();
        classes.add(className);
        classes.add("ScreenObject");

        if (invisible) {
            classes.add("invisible");
        }

        if (decimal) {
            classes.add("decimal");
        }

        attributes.put("id", "obj-" + objectId);

        if (controlPv != null) {
            attributes.put("data-pv", controlPv);
        }

        if (visPv != null) {
            attributes.put("data-vis-pv", visPv);
        }

        if (visInvert) {
            attributes.put("data-vis-invert", "true");
        }

        if (visMin != null) {
            attributes.put("data-vis-min", String.valueOf(visMin));
        }

        if (visMax != null) {
            attributes.put("data-vis-max", String.valueOf(visMax));
        }

        if (alarmPv != null) {
            classes.add("waiting-for-state");

            if (lineAlarm || fillAlarm || fgAlarm || bgAlarm || indicatorAlarm) {
                attributes.put("data-alarm-pv", alarmPv);
            } else {
                attributes.put("data-color-pv", alarmPv);

                if (lineColor != null && lineColor instanceof EDLColorRule) {
                    attributes.put("data-line-color-rule", lineColor.toColorString());
                } 
                
                if (fill && fillColor != null && fillColor instanceof EDLColorRule) {
                    attributes.put("data-fill-color-rule", fillColor.toColorString());
                } 
                
                if (fgColor != null && fgColor instanceof EDLColorRule) {
                    attributes.put("data-fg-color-rule", fgColor.toColorString());
                }
            }
        }

        if (indicatorPv != null) {
            attributes.put("data-indicator-pv", indicatorPv);
        }

        if (onColor != null) {
            attributes.put("data-on-color", onColor.toColorString());
        }

        if (offColor != null) {
            attributes.put("data-off-color", offColor.toColorString());
        }

        if (limitsFromDb) {
            attributes.put("data-limits", "from-db");
        }

        if (indicatorAlarm) {
            attributes.put("data-indicator-alarm", "true");
            classes.add("waiting-for-state");
        }

        if (lineAlarm) {
            attributes.put("data-line-alarm", "true");
        }

        if (fillAlarm) {
            attributes.put("data-fill-alarm", "true");
        }

        if (fgAlarm) {
            attributes.put("data-fg-alarm", "true");
        }

        if (bgAlarm) {
            attributes.put("data-bg-alarm", "true");
        }

        if (max != null) {
            attributes.put("data-max", String.valueOf(max));
        }

        if (min != null) {
            attributes.put("data-min", String.valueOf(min));
        }

        if (origin != null) {
            attributes.put("data-origin", String.valueOf(origin));
        }

        if (fgColor != null) {
            styles.put("color", fgColor.toColorString());
        }

        if (horizontal != null) {
            attributes.put("data-orientation", horizontal ? "horizontal" : "vertical");
        }

        if (font != null) {
            styles.put("font-family", font.name);
            styles.put("font-size", font.size + "px");

            if (font.bold) {
                styles.put("font-weight", "bold");
            }

            if (font.italic) {
                styles.put("font-style", "italic"); // could use oblique here
            }
        }

        String width = String.valueOf(w) + "px";
        String height = String.valueOf(h) + "px";

        if (autoSize) {
            width = "auto";
            height = "auto";
        }

        styles.put("width", width);
        styles.put("height", height);
    }

    public String toHtml(String indent, String indentStep, Point translation) {
        String html = "";

        return html;
    }

    public String getAttributesString(Map<String, String> attributes) {
        String attributesStr = "";

        if (attributes != null && !attributes.isEmpty()) {
            Iterator<String> keys = attributes.keySet().iterator();
            String key = keys.next();
            attributesStr = key + "=\"" + org.apache.taglibs.standard.functions.Functions.escapeXml(
                    attributes.get(key)) + "\"";

            while (keys.hasNext()) {
                key = keys.next();
                attributesStr = attributesStr + " " + key + "=\""
                        + org.apache.taglibs.standard.functions.Functions.escapeXml(attributes.get(
                                key)) + "\"";
            }
        }

        return attributesStr;
    }

    public String getStyleString(Map<String, String> styles) {
        String stylesStr = "";

        if (styles != null && !styles.isEmpty()) {
            Iterator<String> keys = styles.keySet().iterator();
            String key = keys.next();
            stylesStr = key + ": " + org.apache.taglibs.standard.functions.Functions.escapeXml(
                    styles.get(key)) + ";";

            while (keys.hasNext()) {
                key = keys.next();
                stylesStr = stylesStr + " " + key + ": "
                        + org.apache.taglibs.standard.functions.Functions.escapeXml(styles.get(key))
                        + ";";
            }

            stylesStr = "style=\"" + stylesStr + "\"";
        }

        return stylesStr;
    }

    public String getClassString(List<String> classes) {
        String classStr = "";

        if (classes != null && !classes.isEmpty()) {
            classStr = "class=\"" + classes.get(0);

            for (int i = 1; i < classes.size(); i++) {
                classStr = classStr + " " + classes.get(i);
            }

            classStr = classStr + "\"";
        }

        return classStr;
    }
}
