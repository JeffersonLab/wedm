package org.jlab.wedm.widget;

import java.util.Map;
import org.jlab.wedm.persistence.model.ColorPalette;
import org.jlab.wedm.persistence.model.Screen;

/**
 *
 * @author ryans
 */
public class EmbeddedScreen extends ScreenProperties {

    public Screen screen;    
    public String file;
    public String displaySource;
    
    @Override
    public void parseTraits(Map<String, String> traits, ColorPalette palette) {
        super.parseTraits(traits, palette);
        
        file = traits.get("file");
        displaySource = traits.get("displaySource");
    }
}
