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

    private ScreenProperties properties;
    public final List<ScreenObject> screenObjects;
    private final ColorList colorList;

    public Screen(ScreenProperties properties, List<ScreenObject> screenObjects, ColorList colorList) {
        this.properties = properties;
        this.screenObjects = screenObjects;
        this.colorList = colorList;
    }

    public void setScreenProperties(ScreenProperties properties) {
        this.properties = properties;
    }

    public String toHtml(String indent, String indentStep) {

        if (properties.w <= 0) {
            properties.w = 800;
            LOGGER.log(Level.WARNING, "Screen width not defined: using default of 800px");
        }

        if (properties.h <= 0) {
            properties.h = 600;
            LOGGER.log(Level.WARNING, "Screen height not defined: using default of 600px");
        }

        String widthAndHeight = "width: " + properties.w + "px; height: " + properties.h + "px; ";
        String indentPlusOne = indent + indentStep;

        String html
                = indent + "<div class=\"screen\" style=\"position: relative; " + widthAndHeight
                + " ";

        if (properties.bgColor != null && properties.bgColor instanceof EDLColorConstant) {
            html = html + "background-color: "
                    + ((EDLColorConstant) properties.bgColor).toRgbString() + "; ";
        }

        html = html + "\">\n";

        Point translation = new Point(0, 0);

        for (ScreenObject obj : screenObjects) {
            html = html + obj.toHtml(indentPlusOne, indentStep, translation);
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
            js = js + "jlab.wedm.colorRules[" + rule.getIndex() + "] = \"" + rule.getExpression() + "\";\n";
        }
        
        js = js + "jlab.wedm.colors = {};\n";
        
        List<EDLColorConstant> constants = colorList.getStaticColors();
        
        for (EDLColorConstant constant : constants) {
            js = js + "jlab.wedm.colors['" + constant.getName() + "'] = '" + constant.toRgbString() + "';\n";
        }        

        return js;
    }
}
