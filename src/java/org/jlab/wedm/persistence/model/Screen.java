package org.jlab.wedm.persistence.model;

import java.awt.Point;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ryans
 */
public class Screen {

    private static final Logger LOGGER = Logger.getLogger(Screen.class.getName());
    public String indent = "        ";

    private final String canonicalPath;
    private ScreenProperties properties;
    public final List<ScreenObject> screenObjects;
    public Integer embeddedIndex = null;
    private final ColorList colorList;

    public Screen(String canonicalPath, ScreenProperties properties,
            List<ScreenObject> screenObjects, ColorList colorList) {
        this.canonicalPath = canonicalPath;
        this.properties = properties;
        this.screenObjects = screenObjects;
        this.colorList = colorList;
    }

    public void setScreenProperties(ScreenProperties properties) {
        this.properties = properties;
    }

    public HtmlScreen toHtmlScreen() {
        String html = toHtmlBody();
        String css = toCssHead();
        String js = this.getColorStyleVariables(); // TODO: this is wasteful to redo every time
        return new HtmlScreen(canonicalPath, html, css, js, properties.title);
    }

    String toHtmlBody() {

        if (properties.w <= 0) {
            properties.w = 800;
            LOGGER.log(Level.WARNING, "Screen width not defined: using default of 800px");
        }

        if (properties.h <= 0) {
            properties.h = 600;
            LOGGER.log(Level.WARNING, "Screen height not defined: using default of 600px");
        }

        String widthAndHeight = "width: " + properties.w + "px; height: " + properties.h + "px; ";
        String indentPlusOne = indent + HtmlScreen.INDENT_STEP;
        String embeddedIndexStr = "";

        if (embeddedIndex != null) {
            embeddedIndexStr = "data-index=\"" + embeddedIndex + "\"";
        }

        String html
                = indent + "<div class=\"screen\" " + embeddedIndexStr
                + " style=\"position: relative; " + widthAndHeight
                + " ";

        if (properties.bgColor != null && properties.bgColor instanceof EDLColorConstant) {
            html = html + "background-color: "
                    + ((EDLColorConstant) properties.bgColor).toRgbString() + "; ";
        }

        html = html + "\">\n";

        Point translation = new Point(0, 0);

        for (ScreenObject obj : screenObjects) {
            checkForColorRuleWithNoPv(obj);
            html = html + obj.toHtml(indentPlusOne, HtmlScreen.INDENT_STEP, translation);
        }

        html = html + indent + "</div>\n";

        return html;
    }

    public String getColorStyleVariables() {
        String js;

        AlarmColors alarmColors = colorList.getAlarmColors();

        js = "jlab.wedm.disconnectedAlarmColor = '" + alarmColors.disconnectedAlarm.toRgbString()
                + "',\n";
        js = js + "jlab.wedm.invalidAlarmColor = '" + alarmColors.invalidAlarm.toRgbString()
                + "',\n";
        js = js + "jlab.wedm.minorAlarmColor = '" + alarmColors.minorAlarm.toRgbString() + "',\n";
        js = js + "jlab.wedm.majorAlarmColor = '" + alarmColors.majorAlarm.toRgbString() + "',\n";
        js = js + "jlab.wedm.noAlarmColor = '" + alarmColors.noAlarm.toRgbString() + "';\n";

        List<EDLColorRule> rules = colorList.getRuleColors();

        js = js + "jlab.wedm.colorRules = {};\n";

        for (EDLColorRule rule : rules) {
            js = js + "jlab.wedm.colorRules[" + rule.getIndex() + "] = \"" + rule.getExpression()
                    + "\";\n";
        }

        js = js + "jlab.wedm.colors = {};\n";

        List<EDLColorConstant> constants = colorList.getStaticColors();

        for (EDLColorConstant constant : constants) {
            js = js + "jlab.wedm.colors['" + constant.getName() + "'] = '" + constant.toRgbString()
                    + "';\n";
        }

        return js;
    }

    private String toCssHead() {
        String css = "";

        if (properties.fgColor != null && properties.fgColor instanceof EDLColorConstant) {
            css = css + ".ScreenObject:hover {\noutline-color: "
                    + ((EDLColorConstant) properties.fgColor).toRgbString() + " !important;\n}\n";
        }

        return css;
    }

    private void checkForColorRuleWithNoPv(ScreenObject obj) {
        String name;

        if (obj.alarmPv == null) {
            if (obj.lineColor != null && obj.lineColor instanceof EDLColorRule) {
                name = ((EDLColorRule) obj.lineColor).getFirstColor();
                obj.lineColor = colorList.lookup(name);
            }

            if (obj.fill && obj.fillColor != null && obj.fillColor instanceof EDLColorRule) {
                name = ((EDLColorRule) obj.fillColor).getFirstColor();
                obj.fillColor = colorList.lookup(name);
            }

            if (obj.fgColor != null && obj.fgColor instanceof EDLColorRule) {
                name = ((EDLColorRule) obj.fgColor).getFirstColor();
                obj.fgColor = colorList.lookup(name);
            }

            if (obj.onColor != null && obj.onColor instanceof EDLColorRule) {
                name = ((EDLColorRule) obj.onColor).getFirstColor();
                obj.onColor = colorList.lookup(name);
            }
            
            if (obj.offColor != null && obj.offColor instanceof EDLColorRule) {
                name = ((EDLColorRule) obj.offColor).getFirstColor();
                obj.offColor = colorList.lookup(name);
            }            
        }
    }
}
