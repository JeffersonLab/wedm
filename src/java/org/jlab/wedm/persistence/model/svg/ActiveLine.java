package org.jlab.wedm.persistence.model.svg;

import java.awt.Point;

/**
 *
 * @author ryans
 */
public class ActiveLine extends SvgScreenObject {

    public int numPoints;
    public int[] xValues;
    public int[] yValues;
    public boolean closePolygon = false;
    public boolean startArrow = false;
    public boolean endArrow = false;

    @Override
    public String toSvg(String indent, String indentStep, Point translation) {
        String svg = "";
        
        if (xValues != null && yValues != null && xValues.length == yValues.length && xValues.length > 1) {
            
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

            svg = svg + "d=\"M" + xValues[0] + " " + yValues[0] + " ";

            for (int i = 1; i < xValues.length; i++) {
                svg = svg + "L" + xValues[i] + " " + yValues[i] + " ";
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
                
                if(lineWidth != null) {
                    scaleFactor = lineWidth * 2;
                }
                
                float height = 20 + scaleFactor;
                float width = 20 + scaleFactor;
                
                float xPos = xValues[0];
                float yPos = yValues[0] - (height / 2);                
                
                xPos = xPos - 15;
                
                int dx = xValues[0] - xValues[1];
                int dy = yValues[0] - yValues[1];
                double rotate = 0;
                double theta = Math.atan2(dy, dx); 
                rotate = theta *  180 / Math.PI;
                int rotateX = xValues[0];
                int rotateY = yValues[0];
                svg = svg + indent + "<use xlink:href=\"#arrow-head\" x=\"" + xPos + "\" y=\"" + yPos + "\" height=\"" + height + "\" width=\"" + width + "\" transform=\"rotate(" + rotate + " " + rotateX + " " + rotateY + ")\" fill=\"" + strokeColorStr + "\"/>\n";                
            }

            if (endArrow) {
                float scaleFactor = 1;
                
                if(lineWidth != null) {
                    scaleFactor = lineWidth * 2;
                }
                
                float height = 20 + scaleFactor;
                float width = 20 + scaleFactor;
                
                float xPos = xValues[xValues.length - 1];
                float yPos = yValues[yValues.length - 1] - (height / 2);                
                
                xPos = xPos - 15;
                
                int dx = xValues[xValues.length - 1] - xValues[xValues.length - 2];
                int dy = yValues[yValues.length - 1] - yValues[yValues.length - 2];
                double rotate = 0;
                double theta = Math.atan2(dy, dx); 
                rotate = theta *  180 / Math.PI;
                int rotateX = xValues[xValues.length - 1];
                int rotateY = yValues[yValues.length - 1];
                
                svg = svg + indent + "<use xlink:href=\"#arrow-head\" x=\"" + xPos + "\" y=\"" + yPos + "\" height=\"" + height + "\" width=\"" + width + "\" transform=\"rotate(" + rotate + " " + rotateX + " " + rotateY + ")\" fill=\"" + strokeColorStr + "\"/>\n";
            }               
            
            
        }

        return svg;
    }

    private void transformToOrigin(Point translation) {
        for(int i = 0; i < xValues.length; i++) {
            xValues[i] = xValues[i] + translation.x;
            yValues[i] = yValues[i] + translation.y;
        }
    }
}
