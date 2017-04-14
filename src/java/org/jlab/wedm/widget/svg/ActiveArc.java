package org.jlab.wedm.widget.svg;

import java.awt.Point;
import java.util.Map;
import java.util.logging.Logger;
import org.jlab.wedm.persistence.io.TraitParser;
import org.jlab.wedm.persistence.model.ColorPalette;

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
    public void parseTraits(Map<String, String> traits, ColorPalette palette) {
        super.parseTraits(traits, palette);
        
        startAngle = TraitParser.parseInt(traits, "startAngle", 0);
        totalAngle = TraitParser.parseInt(traits, "totalAngle", 180);
    }
    
    @Override
    public String toSvg(String indent, String indentStep, Point translation) {
        String svg = "";

        int originX = x + translation.x;
        int originY = y + translation.y;
        
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

        if (startAngle == null) {
            startAngle = 0;
        }
        
        if (totalAngle == null) {
            totalAngle = 180;
        }
        
        int large = 1;
        int xAxisRotation = 0;
        int sweep = 0;

        int rx = w / 2;
        int ry = h / 2;

        if(totalAngle < 0) {
            sweep = 1;
        }
        
        if(totalAngle <= 180 || totalAngle == 0 || totalAngle == 360) {
            large = 0;
        }           
        
        if(startAngle > 360) {
            startAngle = startAngle % 360;
        }
        
        if(startAngle < -360) {
            startAngle = startAngle % -360;
        }
        
        if(startAngle < 0) {
            startAngle = 360 + startAngle;
        }
        
        //LOGGER.log(Level.FINEST, "startAngle: {0}", startAngle);        
        
        double thetaStart = Math.toRadians(startAngle);

        double startXCalc = rx * Math.cos(thetaStart);
        double startYCalc = ry * Math.sin(thetaStart);

        //LOGGER.log(Level.FINEST, "startXCalc: {0}", startXCalc);
        //LOGGER.log(Level.FINEST, "startYCalc: {0}", startYCalc);

        startYCalc = -startYCalc;        
        
        double startX = startXCalc + originX + (w / 2);
        double startY = startYCalc + originY + (h / 2);     
        
        totalAngle = totalAngle + startAngle;
        
        if(totalAngle > 360) {
            totalAngle = totalAngle % 360;
        }
        
        if(totalAngle < -360) {
            totalAngle = totalAngle % -360;
        }
        
        if(totalAngle < 0) {
            totalAngle = 360 + totalAngle;
        }    
        
        //LOGGER.log(Level.FINEST, "totalAngle: {0}", totalAngle);
        
        double thetaEnd = Math.toRadians(totalAngle);
        double endXCalc = rx * Math.cos(thetaEnd);
        double endYCalc = ry * Math.sin(thetaEnd);

        //LOGGER.log(Level.FINEST, "endXCalc: {0}", endXCalc);
        //LOGGER.log(Level.FINEST, "endYCalc: {0}", endYCalc);

        endYCalc = -endYCalc;

        double endX = endXCalc + originX + (w / 2);
        double endY = endYCalc + originY + (h / 2);
        
        svg = svg + "d=\"M" + startX + "," + startY + " A" + rx + "," + ry + " " + xAxisRotation
                + " " + large + " " + sweep + " " + endX + "," + endY;

        svg = svg + "\"/>\n";

        //svg = svg + "<rect x=\"" + x + "\" y=\"" + y + "\" width=\"" + w + "\" height=\"" + h + "\" stroke=\"red\" fill=\"transparent\" transform=\"rotate(" + startAngle + " " + (x + (w / 2)) + " " + (y + (h / 2)) + ")\"/>\n";
        //svg = svg + "<ellipse cx=\"" + (x + (w / 2)) + "\" cy=\"" + (y + (h / 2)) + "\" rx=\"" + rx + "\" ry=\"" + ry + "\" stroke=\"red\" fill=\"transparent\" transform=\"rotate(" + startAngle + " " + (x + (w / 2)) + " " + (y + (h / 2)) + ")\" stroke-dasharray=\"" + DASH_SPACING + "\"/>\n";
        return svg;
    }
}
