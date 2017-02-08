package org.jlab.wedm.persistence.model.html;

import java.awt.Point;
import org.jlab.wedm.persistence.model.EDLColor;

/**
 *
 * @author ryans
 */
public class ActiveChoiceButton extends HtmlScreenObject {
    public EDLColor selectColor;
    public EDLColor inconsistentColor;
    
    @Override
    public String toHtml(String indent, String indentStep, Point translation) {
        return super.toHtml(indent, indentStep, translation);
    }
}
