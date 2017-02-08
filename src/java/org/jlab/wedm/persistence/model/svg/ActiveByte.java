package org.jlab.wedm.persistence.model.svg;

import java.awt.Point;
import static org.jlab.wedm.persistence.model.svg.SvgScreenObject.DASH_SPACING;

/**
 *
 * @author ryans
 */
public class ActiveByte extends ActiveRectangle {

    public int bits;

    @Override
    public String toSvg(String indent, String indentStep, Point translation) {
        String svg = "";

        //svg = super.toSvg(indent, indentStep, translation);
        int originX = x + translation.x;
        int originY = y + translation.y;

        int width = w;

        if (bits < 1) {
            bits = 1;
        }

        int bitHeight = h / bits;
        
        for (int i = 0; i < bits; i++) {
            svg = svg + indent + "<rect class=\"bit\" x=\"" + originX + "\" y=\"" + originY
                    + "\" width=\"" + width
                    + "\" height=\"" + bitHeight + "\" ";

            String strokeColorStr = "black";

            if (lineColor != null) {
                strokeColorStr = lineColor.toRgbString();
            }

            svg = svg + "stroke=\"" + strokeColorStr + "\" ";

            String fillColorStr = "transparent";

            if (fill && fillColor != null) {
                fillColorStr = fillColor.toRgbString();
            }

            svg = svg + "fill=\"" + fillColorStr + "\" ";

            if (lineWidth != null) {
                svg = svg + "stroke-width=\"" + lineWidth + "\" ";
            }

            if (dash) {
                svg = svg + "stroke-dasharray=\"" + DASH_SPACING + "\" ";
            }

            svg = svg + "/>\n";
            
            originY = originY + bitHeight;
        }

        return svg;
    }
}
