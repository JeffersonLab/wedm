package org.jlab.wedm.persistence.model.html;

import java.awt.Point;

/**
 * CONTROL TEXT
 *
 * @author ryans
 */
public class ActiveControlText extends ActiveStaticText {

    public boolean showUnits = false;    
    
    @Override
    public String toHtml(String indent, String indentStep, Point translation) {
        if(showUnits) {
            attributes.put("data-show-units", "true");
        }
        
        if (alarmPv == null && controlPv != null && (fgAlarm || bgAlarm)) {
            alarmPv = controlPv;
        }

        return super.toHtml(indent, indentStep, translation);
    }

    @Override
    protected void set3DStyles() {
        if (motifWidget) {
            classes.add("motif");

            if (topShadowColor != null) {
                threeDStyles.put("border-bottom", "2px solid " + topShadowColor.toColorString());
                threeDStyles.put("border-right", "2px solid " + topShadowColor.toColorString());
            }

            if (botShadowColor != null) {
                threeDStyles.put("border-top", "2px solid " + botShadowColor.toColorString());
                threeDStyles.put("border-left", "2px solid " + botShadowColor.toColorString());
            }
        }
    }

}
