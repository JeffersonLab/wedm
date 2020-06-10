package org.jlab.wedm.widget.html;

import java.util.Map;
import org.jlab.wedm.widget.ScreenProperties;

/**
 *
 * @author slominskir
 */
public class ActiveEntryText extends ActiveUpdateText {
    @Override
    public void parseTraits(Map<String, String> traits, ScreenProperties properties) {
        super.parseTraits(traits, properties);
        
        motifWidget = true;
        topShadowColor = properties.topShadowColor;
        botShadowColor = properties.botShadowColor;
    }
}
