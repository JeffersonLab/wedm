package org.jlab.wedm.persistence.model.svg;

import java.awt.Point;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ryans
 */
public class ActiveArc extends SvgScreenObject {

    private static final Logger LOGGER = Logger.getLogger(ActiveArc.class.getName());

    public Integer startAngle;
    public Integer totalAngle;
    public boolean pie = false;

    @Override
    public String toSvg(String indent, String indentStep, Point translation) {
        String svg = "";

        int originX = x + translation.x;
        int originY = y + translation.y;
        
        svg = indent + "<path ";

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

        if (startAngle == null) {
            startAngle = 0;
        }
        
        if (totalAngle == null) {
            totalAngle = 180;
        }

        //totalAngle = totalAngle - 90;
        int large = 1;
        int xAxisRotation = 0;
        int sweep = 0;

        int rx = w / 2;
        int ry = h / 2;

        double startX = 0;
        double startY = 0;

        /*if (startAngle != 0 && startAngle != 360) {
            startX = (rx * ry) / Math.sqrt(Math.pow(ry, 2) + Math.pow(rx, 2) * Math.pow(Math.tan(
                    startAngle), 2));
            startY = (rx * ry) / Math.sqrt(Math.pow(rx, 2) + Math.pow(ry, 2) / Math.pow(Math.tan(
                    startAngle), 2));
        }*/
        
        if(startAngle > 360) {
            startAngle = startAngle % 360;
        }
        
        //LOGGER.log(Level.FINEST, "startAngle: {0}", startAngle);        
        
        double thetaStart = Math.toRadians(startAngle);

        double startXCalc = rx * Math.cos(thetaStart);
        double startYCalc = ry * Math.sin(thetaStart);

        //LOGGER.log(Level.FINEST, "startXCalc: {0}", startXCalc);
        //LOGGER.log(Level.FINEST, "startYCalc: {0}", startYCalc);

        startYCalc = -startYCalc;        
        
        if (startAngle == 90) {
            large = 0;
        } else if (startAngle < 90 && startAngle > 0) {
            large = 0;
        } else if (startAngle < 180 && startAngle > 90) {
            large = 0;
        }
        
        startX = startXCalc + originX + (w / 2);
        startY = startYCalc + originY + (h / 2);
        
        
        totalAngle = totalAngle + startAngle;
        
        if(totalAngle > 360) {
            totalAngle = totalAngle % 360;
        }
        
        //LOGGER.log(Level.FINEST, "totalAngle: {0}", totalAngle);
        
        
        //startX = x + w;
        //startY = y + (h / 2);

        /*double endXCalc = (rx * ry) / Math.sqrt(Math.pow(ry, 2) + Math.pow(rx, 2) * Math.pow(Math.tan(
                totalAngle), 2));
        double endYCalc = (rx * ry) / Math.sqrt(Math.pow(rx, 2) + Math.pow(ry, 2) / Math.pow(Math.tan(
                totalAngle), 2));*/
        double thetaEnd = Math.toRadians(totalAngle);

        double endXCalc = rx * Math.cos(thetaEnd);
        double endYCalc = ry * Math.sin(thetaEnd);

        //LOGGER.log(Level.FINEST, "endXCalc: {0}", endXCalc);
        //LOGGER.log(Level.FINEST, "endYCalc: {0}", endYCalc);

        endYCalc = -endYCalc;
        
        if (totalAngle == 90) {
            //LOGGER.log(Level.FINEST, "quad line 90");
            //endYCalc = -endYCalc;
            large = 0;
        } else if (totalAngle == 180) {
            //LOGGER.log(Level.FINEST, "quad line 180");
        } else if (totalAngle == 270) {
            //LOGGER.log(Level.FINEST, "quad line 270");
            //endYCalc = -endYCalc;
        } else if(totalAngle == 360) {
            //LOGGER.log(Level.FINEST, "quad line 360");
            large = 0;
        } else if (totalAngle < 90 && totalAngle > 0) {
            //LOGGER.log(Level.FINEST, "first quadrant");
            //endYCalc = -endYCalc;
            large = 0;
        } else if (totalAngle < 180 && totalAngle > 90) {
            //LOGGER.log(Level.FINEST, "second quadrant");
            //endYCalc = -endYCalc;
            large = 0;
        } else if (totalAngle < 270 && totalAngle > 180) {
            //LOGGER.log(Level.FINEST, "third quadrant");
            //endYCalc = -endYCalc;
        } else if (totalAngle < 360 && totalAngle > 270) {
            //LOGGER.log(Level.FINEST, "fourth quadrant");
            //endYCalc = -endYCalc;
        }

        double endX = endXCalc + originX + (w / 2);
        double endY = endYCalc + originY + (h / 2);

        /*if (totalAngle < (Math.PI / 2) && totalAngle > (-Math.PI / 2)) {
            LOGGER.log(Level.FINEST, "positive");
        } else {
            LOGGER.log(Level.FINEST, "negative");
        }*/

        //svg = svg + "data-start-angle=\"" + startAngle + "\" data-total-angle=\"" + totalAngle
        //        + "\" ";

        /*if(startAngle > 0) {
            svg = svg + "transform=\"rotate(" + -startAngle + " " + (x + (w / 2)) + " " + (y + (h / 2)) + ")\" ";
        }*/
        
        svg = svg + "d=\"M" + startX + "," + startY + " A" + rx + "," + ry + " " + xAxisRotation
                + " " + large + " " + sweep + " " + endX + "," + endY;

        svg = svg + "\"/>\n";

        //svg = svg + "<rect x=\"" + x + "\" y=\"" + y + "\" width=\"" + w + "\" height=\"" + h + "\" stroke=\"red\" fill=\"transparent\" transform=\"rotate(" + startAngle + " " + (x + (w / 2)) + " " + (y + (h / 2)) + ")\"/>\n";
        //svg = svg + "<ellipse cx=\"" + (x + (w / 2)) + "\" cy=\"" + (y + (h / 2)) + "\" rx=\"" + rx + "\" ry=\"" + ry + "\" stroke=\"red\" fill=\"transparent\" transform=\"rotate(" + startAngle + " " + (x + (w / 2)) + " " + (y + (h / 2)) + ")\" stroke-dasharray=\"" + DASH_SPACING + "\"/>\n";
        return svg;
    }
}
