package org.jlab.wedm.widget;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jlab.wedm.persistence.io.EDLParser;
import org.jlab.wedm.persistence.model.ColorPalette;
import org.jlab.wedm.persistence.model.EDLColor;
import org.jlab.wedm.persistence.model.EDLFont;

/**
 *
 * @author ryans
 */
public class ScreenProperties extends CoreWidget {

    private static final Logger LOGGER = Logger.getLogger(ScreenProperties.class.getName());

    public EDLColor textColor;
    public EDLColor ctlFgColor1;
    public EDLColor ctlFgColor2;
    public EDLColor ctlBgColor1;
    public EDLColor ctlBgColor2;
    public EDLFont ctlFont;
    public EDLFont btnFont;
    public String title;

    @Override
    public void parseTraits(Map<String, String> traits, ColorPalette colorList) {
        super.parseTraits(traits, colorList);

        // Colors
        ctlFgColor1 = parseColor("ctlFgColor1", null);
        ctlFgColor2 = parseColor("ctlFgColor2", null);

        // Fonts
        ctlFont = parseFont("ctlFont", EDLParser.DEFAULT_FONT);
        btnFont = parseFont("btnFont", EDLParser.DEFAULT_FONT);

        LOGGER.log(Level.FINEST, "Parsing ScreenProperties");

        for (String key : traits.keySet()) {
            LOGGER.log(Level.FINEST, "Trait: {0}={1}", new Object[]{key, traits.get(key)});
        }
    }
}
