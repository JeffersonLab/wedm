package org.jlab.wedm.widget.html;

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
        classes.add("MouseSensitive");
        
        return super.toHtml(indent, indentStep, translation);
    }
}
