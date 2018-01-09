package org.jlab.wedm.widget;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jlab.wedm.persistence.io.EDLParser;
import org.jlab.wedm.persistence.io.TraitParser;
import org.jlab.wedm.persistence.model.ColorPalette;
import org.jlab.wedm.persistence.model.EDLColor;
import org.jlab.wedm.persistence.model.EDLFont;

/**
 *
 * @author slominskir
 */
public class ScreenProperties extends CoreWidget {

    private static final Logger LOGGER = Logger.getLogger(ScreenProperties.class.getName());

    public EDLColor textColor;
    public EDLColor ctlFgColor1;
    public EDLColor ctlFgColor2;
    public EDLColor ctlBgColor1;
    public EDLColor ctlBgColor2;
    public EDLColor topShadowColor;
    public EDLColor botShadowColor;
    public EDLFont ctlFont;
    public EDLFont btnFont;
    public String title;

    @Override
    public void parseTraits(Map<String, String> traits, ScreenProperties properties) {
        super.parseTraits(traits, properties);
        
        ColorPalette palette = properties.colorList;
        
        // Colors
        textColor = TraitParser.parseColor(traits, palette, "textColor", null);
        ctlFgColor1 = TraitParser.parseColor(traits, palette, "ctlFgColor1", null);
        ctlFgColor2 = TraitParser.parseColor(traits, palette, "ctlFgColor2", null);
        ctlBgColor1 = TraitParser.parseColor(traits, palette, "ctlBgColor1", null);
        ctlBgColor2 = TraitParser.parseColor(traits, palette, "ctlBgColor2", null);
        topShadowColor = TraitParser.parseColor(traits, palette, "topShadowColor", null);
        botShadowColor = TraitParser.parseColor(traits, palette, "botShadowColor", null);
        
        // Fonts
        ctlFont = TraitParser.parseFont(traits, "ctlFont", EDLParser.DEFAULT_FONT);
        btnFont = TraitParser.parseFont(traits, "btnFont", EDLParser.DEFAULT_FONT);
        
        // Strings
        title = traits.get("title");
    }
}
