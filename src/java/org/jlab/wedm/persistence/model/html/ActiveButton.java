package org.jlab.wedm.persistence.model.html;

import java.awt.Point;

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
    public static final String ICON_SYMBOL = "⧉";

    @Override
    public String toHtml(String indent, String indentStep, Point translation) {
        if (push == null) {
            push = false;
        }

        if (!(this instanceof ActiveMessageButton) && !(this instanceof RelatedDisplay)) {
            if (pressValue == null) {
                pressValue = "1";
            }

            if (releaseValue == null) {
                releaseValue = "0";
            }
        }

        if (!push) { // if toggle
            classes.add("toggle-button toggle-button-off");
        } else { // push button
            classes.add("push-button");
        }

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

        if (controlPv != null && controlPv.startsWith("LOC\\")) {
            classes.add("local-control");
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

        align = "center";

        if (bgColor == null) {
            bgColor = offColor;
        }

        return super.toHtml(indent, indentStep, translation);
    }
}
