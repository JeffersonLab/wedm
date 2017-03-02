package org.jlab.wedm.persistence.model.html;

import java.awt.Point;
import org.jlab.wedm.persistence.model.EDLColorConstant;

/**
 *
 * @author ryans
 */
public class ActiveChoiceButton extends HtmlScreenObject {
    public EDLColorConstant selectColor;
    public EDLColorConstant inconsistentColor;
    
    @Override
    public String toHtml(String indent, String indentStep, Point translation) {
        return super.toHtml(indent, indentStep, translation);
    }
}
