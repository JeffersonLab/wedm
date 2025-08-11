package org.jlab.wedm.widget.svg;

import java.awt.Point;
import org.jlab.wedm.persistence.model.EDLAlphaColorConstant;
import org.jlab.wedm.persistence.model.HtmlScreen;

/**
 * @author slominskir
 */
public class ActiveBarMonitor extends ActiveRectangle {

  @Override
  public String toHtml(String indent, Point translation) {
    if (orientation == null) { // ChoiceButton has opposite default...
      orientation = "horizontal";
    }

    if (border) {
      attributes.put("data-border", "true");
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

    if (!border) {
      lineColor = new EDLAlphaColorConstant(0, "transparent", 0, 0, 0, 0);
    }

    svg = super.toSvg(indent, translation);

    int originX = x + translation.x;
    int originY = y + translation.y;

    int width;
    int height;

    int borderPadding = 5;

    int vX = 0;
    int vY = 0;
    int vWidth = 0;
    int vHeight = 0;

    if (border) {
      originX = originX + borderPadding;
      originY = originY + borderPadding;
      width = w - (borderPadding * 2);
      height = h - (borderPadding * 2);
    } else {
      width = w;
      height = h;
    }

    if ("horizontal".equals(orientation)) {

      vX = 0;
      vY = 0;
      vWidth = 0; // Unknown until max/min known = abs(max - origin) + abs(min - origin)
      vHeight = height;

    } else { // Vertical

      int x1 = 0;
      int y1 = height + originY;
      int x2 = w;
      int y2 = height + originY;

      vX = 0;
      vY = height;
      vWidth = width;
      vHeight = 0; // Unknown until max/min known = abs(max - origin) + abs(min - origin)

      // Only vertical has baseline
      svg =
          svg
              + indent
              + "<line class=\"base-line\" x1=\""
              + x1
              + "\" y1=\""
              + y1
              + "\" x2=\""
              + x2
              + "\" y2=\""
              + y2
              + "\" ";

      String strokeColorStr = "black";

      if (indicatorColor != null) {
        strokeColorStr = indicatorColor.toColorString();
      }

      svg = svg + "stroke=\"" + strokeColorStr + "\" style=\"shape-rendering: crispEdges;\"";
      svg = svg + "/>\n";
    }

    svg =
        svg
            + indent
            + "<svg class=\"bar-holder\" x=\""
            + originX
            + "\" y=\""
            + originY
            + "\" width=\""
            + width
            + "\" height=\""
            + height
            + "\" viewBox=\""
            + vX
            + " "
            + vY
            + " "
            + vWidth
            + " "
            + vHeight
            + "\" "
            + "data-border-padding=\""
            + borderPadding
            + "\" preserveAspectRatio=\"none\">\n";
    svg =
        svg
            + indent
            + HtmlScreen.INDENT_STEP
            + "<rect class=\"bar\" x=\""
            + 0
            + "\" y=\""
            + 0
            + "\" width=\""
            + width
            + "\" height=\""
            + height
            + "\" ";

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

    if (!"horizontal".equals(orientation)) {
      svg = svg + "transform=\"scale(1,-1)\"";
    }

    svg = svg + "/>\n";
    svg = svg + indent + "</svg>\n";

    return svg;
  }
}
