package org.jlab.wedm.persistence.model.html;

import java.awt.Point;

/**
 * CONTROL TEXT
 * 
 * @author ryans
 */
public class ActiveXTextDsp extends ActiveXText {
    @Override
    public String toHtml(String indent, String indentStep, Point translation) {
        if(alarmPv == null && controlPv != null && (fgAlarm || bgAlarm)) {
            alarmPv = controlPv;
        }
        
        return super.toHtml(indent, indentStep, translation);
    }
    
}
