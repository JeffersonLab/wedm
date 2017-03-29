package org.jlab.wedm.persistence.model.html;

import java.awt.Point;

/**
 *
 * @author ryans
 */
public class ActiveButton extends TextScreenObject {

    public String buttonLabel = null;
    public String pressValue;
    public String onLabel;
    public String offLabel;
    public boolean push = false; // default is toggle button
    public boolean icon = false;
    public static final String ICON_SYMBOL = "⧉";

    @Override
    public String startHtml(String indent, String indentStep, Point translation) {
        if(!push) { // if toggle
            classes.add("toggle-button-off");
            attributes.put("data-on-label", onLabel);
            attributes.put("data-off-label", offLabel);
        }
        
        if(controlPv != null && controlPv.startsWith("LOC\\")) {
            classes.add("local-control");
        }
        
        return super.startHtml(indent, indentStep, translation);
    }    
    
    @Override
    public String toHtml(String indent, String indentStep, Point translation) {
        if (icon) {
            if (buttonLabel == null) {
                buttonLabel = ICON_SYMBOL;
            } else {
                buttonLabel = ICON_SYMBOL + " " + buttonLabel;
            }
        }
        
        if (buttonLabel != null) {
            value = buttonLabel;
        } else {
            value = offLabel;
        }
        
        align = "center";

        if (bgColor == null) {
            bgColor = onColor;
        }

        return super.toHtml(indent, indentStep, translation);
    }
}
