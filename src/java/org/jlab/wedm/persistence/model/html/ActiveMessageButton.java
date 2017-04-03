package org.jlab.wedm.persistence.model.html;

import java.awt.Point;

/**
 *
 * @author ryans
 */
public class ActiveMessageButton extends ActiveButton {

    @Override
    public String toHtml(String indent, String indentStep, Point translation) {
        if (push == null) {
            push = true;
        }
        
        return super.toHtml(indent, indentStep, translation);
    }
}
