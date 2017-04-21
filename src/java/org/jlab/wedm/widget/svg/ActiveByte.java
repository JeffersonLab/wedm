package org.jlab.wedm.widget.svg;

import java.awt.Point;
import java.util.Map;
import org.jlab.wedm.persistence.io.TraitParser;
import org.jlab.wedm.persistence.model.ColorPalette;
import static org.jlab.wedm.widget.svg.SvgScreenObject.DASH_SPACING;

/**
 *
 * @author ryans
 */
public class ActiveByte extends ActiveRectangle {

    public int numBits;
    public int shift;
    public boolean littleEndian;

    @Override
    public void parseTraits(Map<String, String> traits, ColorPalette palette) {
        super.parseTraits(traits, palette);

        numBits = TraitParser.parseInt(traits, "numBits", 0);
        shift = TraitParser.parseInt(traits, "shift", 0);

        littleEndian = "little".equals(traits.get("endian"));
    }

    @Override
    public String toHtml(String indent, Point translation) {
        attributes.put("data-shift", String.valueOf(shift));
        attributes.put("data-little-endian", String.valueOf(littleEndian));

        return super.toHtml(indent, translation);
    }

    @Override
    public String toSvg(String indent, Point translation) {
        String svg = "";

        //svg = super.toSvg(indent, indentStep, translation);
        int originX = x + translation.x;
        int originY = y + translation.y;

        if (numBits < 1) {
            numBits = 1;
        }

        int bitWidth, bitHeight;
        boolean vertical = true;

        /*If equal h & w EDM makes vertical widget*/
        if (h >= w) {
            bitWidth = w;
            bitHeight = h / numBits;
        } else {
            bitWidth = w / numBits;
            bitHeight = h;
            vertical = false;
        }

        for (int i = 0; i < numBits; i++) {
            svg = svg + indent + "<rect class=\"bit\" x=\"" + originX + "\" y=\"" + originY
                    + "\" width=\"" + bitWidth
                    + "\" height=\"" + bitHeight + "\" ";

            String strokeColorStr = "black";

            if (lineColor != null) {
                strokeColorStr = lineColor.toColorString();
            }

            svg = svg + "stroke=\"" + strokeColorStr + "\" ";

            String fillColorStr = "transparent";

            if (fill && fillColor != null) {
                fillColorStr = fillColor.toColorString();
            }

            svg = svg + "fill=\"" + fillColorStr + "\" ";

            if (lineWidth != null) {
                svg = svg + "stroke-width=\"" + lineWidth + "\" ";
            }

            if (dash) {
                svg = svg + "stroke-dasharray=\"" + DASH_SPACING + "\" ";
            }

            svg = svg + "/>\n";

            if (vertical) {
                originY = originY + bitHeight;
            } else {
                originX = originX + bitWidth;
            }
        }

        return svg;
    }
}
