package org.jlab.wedm.persistence.model.svg;

import java.awt.Point;

/**
 *
 * @author ryans
 */
public class ActiveBarMonitor extends ActiveRectangle {

    @Override
    public String toHtml(String indent, String indentStep, Point translation) {
        if (horizontal == null) { // ChoiceButton has opposite default...
            horizontal = true;
        }

        return super.toHtml(indent, indentStep, translation);
    }

    @Override
    public String toSvg(String indent, String indentStep, Point translation) {
        String svg;

        if (bgColor != null) {
            fill = true;
            fillColor = bgColor;
        }

        svg = super.toSvg(indent, indentStep, translation);

        int originX = x + translation.x;
        int originY = y + translation.y;

        int width;
        int height;
        
        int vX = 0;
        int vY = 0;
        int vWidth = 0;
        int vHeight = 0;

        if (horizontal) {
            originX = originX + w / 24;
            originY = originY + h / 6;
            width = w - (w / 12);
            height = h - (h / 3);
            vX = 0;
            vY = 0;
            vWidth = 0; // Unknown until max/min known = abs(max - origin) + abs(min - origin)
            vHeight = height;          
            
        } else { // Vertical
            originX = originX + w / 6;
            originY = originY + h / 24;
            width = w - (w / 3);
            height = h - (h / 12);
        
            int x1 = 0;
            int y1 = height + originY;
            int x2 = w;
            int y2 = height + originY;
            
            vX = 0;
            vY = height;
            vWidth = width;
            vHeight = 0; // Unknown until max/min known = abs(max - origin) + abs(min - origin)

            // Only vertical has baseline    
            svg = svg + indent + "<line class=\"base-line\" x1=\"" + x1 + "\" y1=\"" + y1
                    + "\" x2=\"" + x2 + "\" y2=\"" + y2 + "\" ";

            String strokeColorStr = "black";

            if (indicatorColor != null) {
                strokeColorStr = indicatorColor.toRgbString();
            }

            svg = svg + "stroke=\"" + strokeColorStr + "\" ";
            svg = svg + "/>\n";
        }

        svg = svg + indent + "<svg class=\"bar-holder\" x=\"" + originX + "\" y=\"" + originY
                + "\" width=\"" + width
                + "\" height=\"" + height + "\" viewBox=\"" + vX + " " + vY + " " + vWidth + " " + vHeight + "\" preserveAspectRatio=\"none\">\n";
        svg = svg + indent + indentStep + "<rect class=\"bar\" x=\"" + 0 + "\" y=\"" + 0
                + "\" width=\"" + width
                + "\" height=\"" + height + "\" ";

        String fillColorStr = "transparent";

        if (fill && fillColor != null) {
            fillColorStr = fillColor.toRgbString();
        }

        if (indicatorColor != null) {
            fillColorStr = indicatorColor.toRgbString();
        }

        svg = svg + "fill=\"" + fillColorStr + "\" ";

        if (lineWidth != null) {
            svg = svg + "stroke-width=\"" + lineWidth + "\" ";
        }

        if (dash) {
            svg = svg + "stroke-dasharray=\"" + DASH_SPACING + "\" ";
        }

        if(!horizontal) {
            svg = svg + "transform=\"scale(1,-1)\"";
        }
        
        svg = svg + "/>\n";
        svg = svg + indent + "</svg>\n";

        return svg;
    }
}
