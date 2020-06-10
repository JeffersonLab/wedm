package org.jlab.wedm.persistence.model;

import java.awt.Point;
import java.util.Map;
import org.jlab.wedm.widget.ScreenProperties;

/**
 *
 * @author slominskir
 */
public interface WEDMWidget {
    public void parseTraits(Map<String, String> traits, ScreenProperties properties);    
    public String toHtml(String indent, Point translation);
    public void symbolColorOverride(EDLColor bgColor, EDLColor fgColor);
    public void performColorRuleCorrection();
    public Map<String, String> getTraits();
}
