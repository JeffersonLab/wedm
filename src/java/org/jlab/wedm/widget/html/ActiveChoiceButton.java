package org.jlab.wedm.widget.html;

import java.awt.Point;
import java.util.Map;
import org.jlab.wedm.persistence.io.TraitParser;
import org.jlab.wedm.persistence.model.ColorPalette;
import org.jlab.wedm.persistence.model.EDLColor;

/**
 *
 * @author ryans
 */
public class ActiveChoiceButton extends HtmlScreenObject {

    public EDLColor selectColor;
    public EDLColor inconsistentColor;

    @Override
    public void parseTraits(Map<String, String> traits, ColorPalette palette) {
        super.parseTraits(traits, palette);
        
        selectColor = TraitParser.parseColor(traits, palette, "selectColor", null);
    }
    
    @Override
    public String toHtml(String indent, String indentStep, Point translation) {
        classes.add("MouseSensitive");

        if (controlPv != null && controlPv.startsWith("LOC\\")) {
            classes.add("interactable");
        } else {
            classes.add("non-interactable");
        }

        if (topShadowColor != null && botShadowColor != null) {
            attributes.put("data-bot-shad-color", botShadowColor.toColorString());
            attributes.put("data-top-shad-color", topShadowColor.toColorString());
        }
        
        if(selectColor != null) {
            attributes.put("data-select-color", selectColor.toColorString());
        }

        return super.toHtml(indent, indentStep, translation);
    }
}
