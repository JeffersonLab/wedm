package org.jlab.wedm.widget.svg;

import java.awt.Point;
import org.jlab.wedm.persistence.model.HtmlScreen;

/**
 *
 * @author ryans
 */
public class ActiveBarMonitor extends ActiveRectangle {

    @Override
    public String toHtml(String indent, Point translation) {
        if (orientation == null) { // ChoiceButton has opposite default...
            orientation = "horizontal";
        }

        return super.toHtml(indent, translation);
    }

    @Override
    public String toSvg(String indent, Point translation) {
        String svg;

        if (bgColor != null) {
            fill = true;
            fillColor = bgColor;
        }

        svg = super.toSvg(indent, translation);

        int originX = x + translation.x;
        int originY = y + translation.y;

        int width;
        int height;
        
        int verticalPadding = 0;
        
        int vX = 0;
        int vY = 0;
        int vWidth = 0;
        int vHeight = 0;

        if ("horizontal".equals(orientation)) {
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
        
            verticalPadding = originY;
            
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
                strokeColorStr = indicatorColor.toColorString();
            }

            svg = svg + "stroke=\"" + strokeColorStr + "\" ";
            svg = svg + "/>\n";
        }

        svg = svg + indent + "<svg class=\"bar-holder\" x=\"" + originX + "\" y=\"" + originY
                + "\" width=\"" + width
                + "\" height=\"" + height + "\" viewBox=\"" + vX + " " + vY + " " + vWidth + " " + vHeight + "\" "
                + "data-vertical-padding=\"" + verticalPadding + "\" preserveAspectRatio=\"none\">\n";
        svg = svg + indent + HtmlScreen.INDENT_STEP + "<rect class=\"bar\" x=\"" + 0 + "\" y=\"" + 0
                + "\" width=\"" + width
                + "\" height=\"" + height + "\" ";

        String fillColorStr = "transparent";

        if (fill && fillColor != null) {
            fillColorStr = fillColor.toColorString();
        }

        if (indicatorColor != null) {
            fillColorStr = indicatorColor.toColorString();
        }

        svg = svg + "fill=\"" + fillColorStr + "\" ";

        if (lineWidth != null) {
            svg = svg + "stroke-width=\"" + lineWidth + "\" ";
        }

        if (dash) {
            svg = svg + "stroke-dasharray=\"" + DASH_SPACING + "\" ";
        }

        if(!"horizontal".equals(orientation)) {
            svg = svg + "transform=\"scale(1,-1)\"";
        }
        
        svg = svg + "/>\n";
        svg = svg + indent + "</svg>\n";

        return svg;
    }
}
