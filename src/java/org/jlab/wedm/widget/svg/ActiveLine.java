package org.jlab.wedm.widget.svg;

import java.awt.Point;
import java.util.Map;
import org.jlab.wedm.persistence.io.TraitParser;
import org.jlab.wedm.persistence.model.ColorPalette;
import org.jlab.wedm.widget.ScreenProperties;

/**
 *
 * @author slominskir
 */
public class ActiveLine extends SvgScreenObject {

    public int numPoints;
    public int[] xPoints;
    public int[] yPoints;
    public boolean closePolygon;
    public boolean startArrow;
    public boolean endArrow;


    @Override
    public void parseTraits(Map<String, String> traits, ScreenProperties properties) {
        super.parseTraits(traits, properties);
        
        numPoints = TraitParser.parseInt(traits, "numPoints", 0);
        
        xPoints = TraitParser.parseIntArray(traits, numPoints, "xPoints");
        yPoints = TraitParser.parseIntArray(traits, numPoints, "yPoints");
        
        closePolygon = TraitParser.parseBoolean(traits, "closePolygon");
        
        String arrows = traits.get("arrows");
        
        if("both".equals(arrows)) {
            startArrow = true;
            endArrow = true;           
        } else if("from".equals(arrows)) {
            startArrow = true;
        } else if("to".equals(arrows)) {
            endArrow = true;
        }

    }
    
    @Override
    public String toSvg(String indent, Point translation) {
        String svg = "";

        if (xPoints != null && yPoints != null && xPoints.length == yPoints.length && xPoints.length
                > 1) {

            transformToOrigin(translation);

            svg = indent + "<path ";

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

            svg = svg + "d=\"M" + xPoints[0] + " " + yPoints[0] + " ";

            for (int i = 1; i < xPoints.length; i++) {
                svg = svg + "L" + xPoints[i] + " " + yPoints[i] + " ";
            }

            if (closePolygon) {
                svg = svg + "z ";
            }

            svg = svg + "\"/>\n";

            // Can't use SVG markers for line arrows for two reasons:
            // 1. SVG markers do not inherit line color (SVG 1.2 propsoal has fill="content-stroke", but it is currently unsupported in all browsers
            // 2. SVG markers scale at a linear factor based on line width (looks like default ~ 1:1, but w/viewBox and markerHeight/Width attr can change factor, but only linearly - also option for don't scale at all exists w/markerUnits "userSpaceOnUse").  Linear scaling does NOT match EDM, which is less than linear scaling
            if (startArrow) {
                float scaleFactor = 1;

                if (lineWidth != null) {
                    scaleFactor = lineWidth * 2;
                }

                float height = 20 + scaleFactor;
                float width = 20 + scaleFactor;

                float xPos = xPoints[0];
                float yPos = yPoints[0] - (height / 2);

                xPos = xPos - 15;

                int dx = xPoints[0] - xPoints[1];
                int dy = yPoints[0] - yPoints[1];
                double rotate = 0;
                double theta = Math.atan2(dy, dx);
                rotate = theta * 180 / Math.PI;
                int rotateX = xPoints[0];
                int rotateY = yPoints[0];
                svg = svg + indent + "<use xlink:href=\"#arrow-head\" x=\"" + xPos + "\" y=\""
                        + yPos + "\" height=\"" + height + "\" width=\"" + width
                        + "\" transform=\"rotate(" + rotate + " " + rotateX + " " + rotateY
                        + ")\" fill=\"" + strokeColorStr + "\"/>\n";
            }

            if (endArrow) {
                float scaleFactor = 1;

                if (lineWidth != null) {
                    scaleFactor = lineWidth * 2;
                }

                float height = 20 + scaleFactor;
                float width = 20 + scaleFactor;

                float xPos = xPoints[xPoints.length - 1];
                float yPos = yPoints[yPoints.length - 1] - (height / 2);

                xPos = xPos - 15;

                int dx = xPoints[xPoints.length - 1] - xPoints[xPoints.length - 2];
                int dy = yPoints[yPoints.length - 1] - yPoints[yPoints.length - 2];
                double rotate = 0;
                double theta = Math.atan2(dy, dx);
                rotate = theta * 180 / Math.PI;
                int rotateX = xPoints[xPoints.length - 1];
                int rotateY = yPoints[yPoints.length - 1];

                svg = svg + indent + "<use xlink:href=\"#arrow-head\" x=\"" + xPos + "\" y=\""
                        + yPos + "\" height=\"" + height + "\" width=\"" + width
                        + "\" transform=\"rotate(" + rotate + " " + rotateX + " " + rotateY
                        + ")\" fill=\"" + strokeColorStr + "\"/>\n";
            }

        }

        return svg;
    }

    private void transformToOrigin(Point translation) {
        for (int i = 0; i < xPoints.length; i++) {
            xPoints[i] = xPoints[i] + translation.x;
            yPoints[i] = yPoints[i] + translation.y;
        }
    }
}
