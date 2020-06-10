package org.jlab.wedm.persistence.model;

import org.jlab.wedm.widget.ScreenProperties;
import java.awt.Point;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author slominskir
 */
public class Screen {

    private static final Logger LOGGER = Logger.getLogger(Screen.class.getName());

    private final String canonicalPath;
    private final long modifiedDate;
    private ScreenProperties properties;
    public final List<WEDMWidget> screenObjects;
    public Integer embeddedIndex = null;
    private final ColorPalette colorList;

    public Screen(String canonicalPath, long modifiedDate, ScreenProperties properties,
            List<WEDMWidget> screenObjects, ColorPalette colorList) {
        this.canonicalPath = canonicalPath;
        this.modifiedDate = modifiedDate;
        this.properties = properties;
        this.screenObjects = screenObjects;
        this.colorList = colorList;
    }

    public void setScreenProperties(ScreenProperties properties) {
        this.properties = properties;
    }

    public HtmlScreen toHtmlScreen() {
        String html = toHtmlBody(HtmlScreen.INITIAL_INDENT);
        String css = toCssHead();
        String js = this.getColorStyleVariables(); // TODO: this is wasteful to redo every time
        return new HtmlScreen(canonicalPath, modifiedDate, html, css, js, properties.title);
    }

    public String toHtmlBody(String indent) {

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

        for (WEDMWidget obj : screenObjects) {
            html = html + obj.toHtml(indentPlusOne, translation);
        }

        html = html + HtmlScreen.INITIAL_INDENT + "</div>\n";

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
            // Someday we may want to add: ,\nbody .ScreenObject .hoverable-part:hover:before
            css = css + "body .ScreenObject:hover,\nbody .ScreenObject:hover:before {\noutline-color: "
                    + ((EDLColorConstant) properties.fgColor).toRgbString() + " !important;\n}\n";
        }

        return css;
    }
    
    @Override
    public String toString(){
        return canonicalPath;
    }
}
