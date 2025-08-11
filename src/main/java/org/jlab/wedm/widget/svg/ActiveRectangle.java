package org.jlab.wedm.widget.svg;

import java.awt.Point;

/**
 * @author slominskir
 */
public class ActiveRectangle extends SvgScreenObject {

  @Override
  public String toSvg(String indent, Point translation) {
    String svg;

    int originX = x + translation.x;
    int originY = y + translation.y;

    svg =
        indent
            + "<rect x=\""
            + originX
            + "\" y=\""
            + originY
            + "\" width=\""
            + w
            + "\" height=\""
            + h
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
