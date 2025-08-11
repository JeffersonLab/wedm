package org.jlab.wedm.widget.svg;

import java.awt.Point;

/**
 * @author slominskir
 */
public class ActiveCircle extends SvgScreenObject {
  @Override
  public String toSvg(String indent, Point translation) {
    String svg = "";

    int originX = x + translation.x;
    int originY = y + translation.y;

    int rx = w / 2;
    int ry = h / 2;
    int cx = originX + rx;
    int cy = originY + ry;

    svg =
        indent
            + "<ellipse cx=\""
            + cx
            + "\" cy=\""
            + cy
            + "\" rx=\""
            + rx
            + "\" ry=\""
            + ry
            + "\" ";

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

    return svg;
  }
}
