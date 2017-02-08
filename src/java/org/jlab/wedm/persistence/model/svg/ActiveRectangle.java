package org.jlab.wedm.persistence.model.svg;

import java.awt.Point;

/**
 *
 * @author ryans
 */
public class ActiveRectangle extends SvgScreenObject {

    @Override
    public String toSvg(String indent, String indentStep, Point translation) {
        String svg;

        int originX = x + translation.x;
        int originY = y + translation.y;

        svg = indent + "<rect x=\"" + originX + "\" y=\"" + originY + "\" width=\"" + w
                + "\" height=\"" + h + "\" ";

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
            
            if(lineWidth < 1.8) {
                lineWidth = 1.8f; // This is due to EDM rendering 0 width border as 1.8
            }
            
            svg = svg + "stroke-width=\"" + lineWidth + "\" ";
        }

        if (dash) {
            svg = svg + "stroke-dasharray=\"" + DASH_SPACING + "\" ";
        }

        svg = svg + "/>\n";

        return svg;
    }
}
