package org.jlab.wedm.widget.html;

import java.util.Map;
import org.jlab.wedm.widget.ScreenProperties;

/**
 *
 * @author slominskir
 */
public class ActiveUpdateText extends ActiveControlText {

    public String displayMode = "default";

    @Override
    public void parseTraits(Map<String, String> traits, ScreenProperties properties) {
        super.parseTraits(traits, properties);

        displayMode = traits.get("displayMode");

        if (displayMode == null) {
            attributes.put("data-show-units", "true");
        }

        if ("exp".equals(displayMode) || "engineer".equals(displayMode)) {
            format = "exponential";

            // Special case: We explicity default precision to zero if not defined (but only if scientific notation!)
            if (precision == null) {
                precision = 0;
            }
        }
        
        if(!fill) {
            bgColor = null;
        }
        
        // When alarm sensitive and no alarm, use regular foreground color
        attributes.put("data-no-alarm-color", fgColor.toColorString());
    }
}
