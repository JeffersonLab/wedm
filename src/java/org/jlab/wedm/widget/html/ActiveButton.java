package org.jlab.wedm.widget.html;

import java.awt.Point;
import java.util.Map;
import org.jlab.wedm.persistence.io.TraitParser;
import org.jlab.wedm.persistence.model.ColorPalette;

/**
 *
 * @author ryans
 */
public class ActiveButton extends TextScreenObject {

    public String buttonLabel = null;
    public String pressValue = null;
    public String releaseValue = null;
    public String onLabel;
    public String offLabel;    
    public Boolean push = null; // default is different for different widgets!  ActiveButton is false while ActiveMessageButton is true.
    public boolean icon = false;
    public boolean swapButtons = false; /*Only RelatedDisplay and ShellCommand use this*/    
    public static final String ICON_SYMBOL = "⧉";

    @Override
    public void parseTraits(Map<String, String> traits, ColorPalette colorList) {
        super.parseTraits(traits, colorList);
        
        // Strings
        buttonLabel = traits.get("buttonLabel");
        pressValue = traits.get("pressValue");
        releaseValue = traits.get("releaseValue");
        onLabel = traits.get("onLabel");
        offLabel = traits.get("offLabel");
        
        // Booleans
        icon = TraitParser.parseBoolean(traits, "icon");
        swapButtons = TraitParser.parseBoolean(traits, "swapButtons");
        
        // Determine if push or toggle -> this is harder than it should be
        if(traits.get("buttonType") != null) { // ActiveButton looks for buttonType: "push" and has default of toggle
            push = "push".equals(traits.get("buttonType"));
        } else if(traits.get("toggle") != null) {
            push = false;  // ActiveMessageButton looks for toggle and has default of push
        }
    }
    
    protected void setActionValues() {
        if (!(this instanceof ActiveMessageButton) && !(this instanceof RelatedDisplay)) {
            if (pressValue == null) {
                pressValue = "1";
            }

            if (releaseValue == null) {
                releaseValue = "0";
            }
        }        
    }
    
    protected void setButtonType() {
        if (!push) { // if toggle
            classes.add("toggle-button toggle-button-off");
        } else { // push button
            classes.add("push-button");
        }        
    }
    
    protected void setInteractable() {
        if (controlPv != null && controlPv.startsWith("LOC\\")) {
            classes.add("interactable");
        } else {
            classes.add("disabled-interactable");
        }        
    }
    
    @Override
    public String toHtml(String indent, String indentStep, Point translation) {
        if (push == null) {
            push = false;
        }

        classes.add("MouseSensitive");
        
        setActionValues();

        setButtonType();

        setInteractable();
        
        if (pressValue != null) {
            attributes.put("data-press-value", pressValue);
        }

        if (releaseValue != null) {
            attributes.put("data-release-value", releaseValue);
        }

        if (onLabel != null) {
            attributes.put("data-on-label", onLabel);
        }

        if (offLabel != null) {
            attributes.put("data-off-label", offLabel);
        }

        if (onColor != null) {
            attributes.put("data-on-color", onColor.toColorString());
        }
        if (offColor != null) {
            attributes.put("data-off-color", offColor.toColorString());
        }

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

        fontAlign = "center";

        if (bgColor == null) {
            bgColor = offColor;
        }

        return super.toHtml(indent, indentStep, translation);
    }
}
