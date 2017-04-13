package org.jlab.wedm.widget.svg;

import java.awt.Point;
import static org.jlab.wedm.widget.svg.SvgScreenObject.DASH_SPACING;

/**
 *
 * @author ryans
 */
public class ActiveByte extends ActiveRectangle {

    public int bits = 0;
    public int shift = 0;
    public boolean littleEndian = false;

    @Override
    public String toHtml(String indent, String indentStep, Point translation) {
        attributes.put("data-shift", String.valueOf(shift));
        attributes.put("data-little-endian", String.valueOf(littleEndian));
        
        return super.toHtml(indent, indentStep, translation);
    }

    @Override
    public String toSvg(String indent, String indentStep, Point translation) {
        String svg = "";

        //svg = super.toSvg(indent, indentStep, translation);
        int originX = x + translation.x;
        int originY = y + translation.y;

        if (bits < 1) {
            bits = 1;
        }

        int bitWidth, bitHeight;
        boolean vertical = true;

        /*If equal h & w EDM makes vertical widget*/
        if (h >= w) {
            bitWidth = w;
            bitHeight = h / bits;
        } else {
            bitWidth = w / bits;
            bitHeight = h;
            vertical = false;
        }

        for (int i = 0; i < bits; i++) {
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
