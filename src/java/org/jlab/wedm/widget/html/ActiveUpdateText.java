package org.jlab.wedm.widget.html;

import java.util.Map;
import org.jlab.wedm.persistence.model.ColorPalette;

/**
 *
 * @author ryans
 */
public class ActiveUpdateText extends ActiveControlText {

    public String displayMode = "default";

    @Override
    public void parseTraits(Map<String, String> traits, ColorPalette colorList) {
        super.parseTraits(traits, colorList);

        displayMode = traits.get("displayMode");

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
    }
}
