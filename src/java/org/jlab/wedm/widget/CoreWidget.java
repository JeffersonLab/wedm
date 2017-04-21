package org.jlab.wedm.widget;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jlab.wedm.persistence.io.EDLParser;
import org.jlab.wedm.persistence.io.TraitParser;
import org.jlab.wedm.persistence.model.ColorPalette;
import org.jlab.wedm.persistence.model.EDLColor;
import org.jlab.wedm.persistence.model.EDLColorRule;
import org.jlab.wedm.persistence.model.EDLFont;
import org.jlab.wedm.persistence.model.WEDMWidget;

/**
 *
 * @author ryans
 */
public abstract class CoreWidget implements WEDMWidget {

    private static final Logger LOGGER = Logger.getLogger(CoreWidget.class.getName());

    public ColorPalette colorList;
    public Map<String, String> traits;
    public int objectId;
    public int x;
    public int y;
    public int w;
    public int h;
    public int numPvs = 0;
    public int numDsps = 0;
    public String[] displayFileName;
    public String[] menuLabel;
    public String[] symbols;
    public Integer precision = null;
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
    public boolean motifWidget = false;
    public boolean fill = false;
    public boolean dash = false;
    public boolean visInvert = false;
    public boolean autoSize = false;
    public boolean border = false;
    public boolean limitsFromDb = false;
    public boolean indicatorAlarm = false;
    public boolean lineAlarm = false;
    public boolean fillAlarm = false;
    public boolean fgAlarm = false;
    public boolean bgAlarm = false;
    public boolean useAlarmBorder = false;
    public boolean editable = false;
    public boolean useHexPrefix = false;
    public EDLFont font;
    public String format;
    public String controlPv;
    public String visPv;
    public String alarmPv;
    public String indicatorPv;
    public String orientation;

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

        if (editable) {
            classes.add("editable");
        }

        attributes.put("id", "obj-" + objectId);

        if (format != null) {
            attributes.put("data-format", format);

            if ("hex".equals(format) && useHexPrefix) {
                attributes.put("data-hex-prefix", "true");
            }
        }

        if (precision != null) {
            attributes.put("data-precision", String.valueOf(precision));
        }

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

        if (useAlarmBorder) { // Make sure this statement is executed before fgAlarm statement below
            attributes.put("data-border-alarm", "true");
            fgAlarm = false; // EDM ignores foreground alarms if there is a border alarm!
            h = h + 2;
            //w = w + 2;
        }

        if (alarmPv != null) {
            classes.add("waiting-for-state");

            if (lineAlarm || fillAlarm || fgAlarm || bgAlarm || indicatorAlarm || useAlarmBorder) {
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

                if (bgColor != null && bgColor instanceof EDLColorRule) {
                    attributes.put("data-bg-color-rule", bgColor.toColorString());
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
            attributes.put("data-db-limits", "true");
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

        if (orientation != null) {
            attributes.put("data-orientation", orientation);
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

    @Override
    public String toHtml(String indent, Point translation) {
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

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append("ID: ");
        builder.append(objectId);
        builder.append(", FillColor: ");
        builder.append(fillColor == null ? "" : fillColor.toColorString());

        return builder.toString();
    }

    @Override
    public void parseTraits(Map<String, String> traits, ColorPalette palette) {
        this.traits = traits;
        this.colorList = palette;
        
        if (traits != null) {
            //LOGGER.log(Level.FINEST, "Parsing core traits for: {0}", this.getClass().getSimpleName());

            // WIDGET ID
            objectId = TraitParser.parseInt(traits, "WEDM_WIDGET_ID", -1);
            
            // Dimensions and Coordinates
            x = TraitParser.parseInt(traits, "x", 0);
            y = TraitParser.parseInt(traits, "y", 0);
            w = TraitParser.parseInt(traits, "w", 0);
            h = TraitParser.parseInt(traits, "h", 0);

            // Other ints
            numPvs = TraitParser.parseInt(traits, "numPvs", 0);
            numDsps = TraitParser.parseInt(traits, "numDsps", 0);
            precision = TraitParser.parseInt(traits, "precision", null);

            // Colors
            bgColor = TraitParser.parseColor(traits, palette, "bgColor", null);
            fgColor = TraitParser.parseColor(traits, palette, "fgColor", null);
            topShadowColor = TraitParser.parseColor(traits, palette, "topShadowColor", null);
            botShadowColor = TraitParser.parseColor(traits, palette, "botShadowColor", null);
            lineColor = TraitParser.parseColor(traits, palette, "lineColor", null);
            fillColor = TraitParser.parseColor(traits, palette, "fillColor", null);
            onColor = TraitParser.parseColor(traits, palette, "onColor", null);
            offColor = TraitParser.parseColor(traits, palette, "offColor", null);
            indicatorColor = TraitParser.parseColor(traits, palette, "indicatorColor", null);

            // Fonts
            font = TraitParser.parseFont(traits, "font", EDLParser.DEFAULT_FONT);

            // Boolean Flags
            invisible = TraitParser.parseBoolean(traits, "invisible");
            visInvert = TraitParser.parseBoolean(traits, "visInvert");
            useDisplayBg = TraitParser.parseBoolean(traits, "useDisplayBg");
            motifWidget = TraitParser.parseBoolean(traits, "motifWidget");
            fill = TraitParser.parseBoolean(traits, "fill");
            autoSize = TraitParser.parseBoolean(traits, "autoSize");
            border = TraitParser.parseBoolean(traits, "border");
            limitsFromDb = TraitParser.parseBoolean(traits, "limitsFromDb");
            indicatorAlarm = TraitParser.parseBoolean(traits, "indicatorAlarm");
            lineAlarm = TraitParser.parseBoolean(traits, "lineAlarm");
            fillAlarm = TraitParser.parseBoolean(traits, "fillAlarm");
            fgAlarm = TraitParser.parseBoolean(traits, "fgAlarm");
            bgAlarm = TraitParser.parseBoolean(traits, "bgAlarm");
            useAlarmBorder = TraitParser.parseBoolean(traits, "useAlarmBorder");
            editable = TraitParser.parseBoolean(traits, "editable");
            useHexPrefix = TraitParser.parseBoolean(traits, "useHexPrefix");

            // Convenience booleans
            dash = "dash".equals(traits.get("lineStyle"));
            
            // Floats
            visMin = TraitParser.parseFloat(traits, "visMin", null);
            visMax = TraitParser.parseFloat(traits, "visMax", null);
            lineWidth = TraitParser.parseFloat(traits, "lineWidth", null);
            max = TraitParser.parseFloat(traits, "max", null);
            min = TraitParser.parseFloat(traits, "min", null);
            origin = TraitParser.parseFloat(traits, "origin", null);

            // Strings
            format = traits.get("format");
            controlPv = traits.get("controlPv");
            visPv = traits.get("visPv");
            alarmPv = traits.get("alarmPv");

            if (alarmPv == null) { // ActiveButton is different
                alarmPv = traits.get("colorPv");
            }

            indicatorPv = traits.get("indicatorPv");
            orientation = traits.get("orientation");
            
            // String arrays
            displayFileName = TraitParser.parseStringArray(traits, numDsps, "displayFileName");
            menuLabel = TraitParser.parseStringArray(traits, numDsps, "menuLabel");
            symbols = TraitParser.parseStringArray(traits, numDsps, "symbols");
        }
    }

    @Override
    public void symbolColorOverride(EDLColor bgColor, EDLColor fgColor) {
        this.fgColor = fgColor;
        this.lineColor = fgColor;

        this.bgColor = bgColor;
        this.fillColor = bgColor;
    }

    @Override
    public void performColorRuleCorrection() {
        String name;

        if (alarmPv == null) {
            if (lineColor != null && lineColor instanceof EDLColorRule) {
                name = ((EDLColorRule) lineColor).getFirstColor();
                lineColor = colorList.lookup(name);
            }

            if (fill && fillColor != null
                    && fillColor instanceof EDLColorRule) {
                name = ((EDLColorRule) fillColor).getFirstColor();
                fillColor = colorList.lookup(name);
            }

            if (fgColor != null && fgColor instanceof EDLColorRule) {
                name = ((EDLColorRule) fgColor).getFirstColor();
                fgColor = colorList.lookup(name);
            }

            if (onColor != null && onColor instanceof EDLColorRule) {
                name = ((EDLColorRule) onColor).getFirstColor();
                onColor = colorList.lookup(name);
            }

            if (offColor != null && offColor instanceof EDLColorRule) {
                name = ((EDLColorRule) offColor).getFirstColor();
                offColor = colorList.lookup(name);
            }
        }
    }
}
