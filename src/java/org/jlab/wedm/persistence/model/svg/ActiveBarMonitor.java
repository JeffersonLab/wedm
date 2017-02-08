package org.jlab.wedm.persistence.model.svg;

import java.awt.Point;

/**
 *
 * @author ryans
 */
public class ActiveBarMonitor extends ActiveRectangle {
    @Override
    public String toSvg(String indent, String indentStep, Point translation) {
        String svg;
        
        if(bgColor != null) {
            fill = true;
            fillColor = bgColor;
        }
        
        svg = super.toSvg(indent, indentStep, translation);
        
        int originX = x + translation.x;
        int originY = y + translation.y;

        originX = originX + w / 4;
        originY = originY + h / 16;
        
        int width = w / 2;
        int height = h - (h / 8);
        
        int x1 = 0;
        int y1 = height + originY;
        int x2 = w;
        int y2 = height + originY;
        
        svg = svg + indent + "<line class=\"base-line\" x1=\"" + x1 + "\" y1=\"" + y1
                + "\" x2=\"" + x2 + "\" y2=\"" + y2 + "\" "; 
        
        String strokeColorStr = "black";

        if (indicatorColor != null) {
            strokeColorStr = indicatorColor.toRgbString();
        }

        svg = svg + "stroke=\"" + strokeColorStr + "\" ";        
        
        svg = svg + "/>\n";
        
        svg = svg + indent + "<svg class=\"bar-holder\" x=\"" + originX + "\" y=\"" + originY + "\" width=\"" + width
                + "\" height=\"" + height + "\" viewBox=\"0 " + height + " " + width + " 100\">\n";
        svg = svg + indent + indentStep + "<rect class=\"bar\" x=\"" + 0 + "\" y=\"" + 0 + "\" width=\"" + width
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

        svg = svg + "transform=\"scale(1,-1)\"";
        
        svg = svg + "/>\n";
        svg = svg + indent + "</svg>\n";
        
        return svg;
    }
}
