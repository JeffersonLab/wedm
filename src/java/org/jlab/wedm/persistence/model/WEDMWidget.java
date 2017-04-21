package org.jlab.wedm.persistence.model;

import java.awt.Point;
import java.util.Map;

/**
 *
 * @author ryans
 */
public interface WEDMWidget {
    public void parseTraits(Map<String, String> traits, ColorPalette palette);    
    public String toHtml(String indent, Point translation);
    public void symbolColorOverride(EDLColor bgColor, EDLColor fgColor);
    public void performColorRuleCorrection();
}
