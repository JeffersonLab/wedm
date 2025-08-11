package org.jlab.wedm.widget.html;

import java.awt.Point;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jlab.wedm.persistence.io.TraitParser;
import org.jlab.wedm.widget.CoreWidget;
import org.jlab.wedm.widget.ScreenProperties;

/**
 * @author slominskir
 */
public class HtmlScreenObject extends CoreWidget {

  private static final Logger LOGGER = Logger.getLogger(HtmlScreenObject.class.getName());

  public boolean threeDimensional = false; // might only apply to ActiveButton?

  @Override
  public void parseTraits(Map<String, String> traits, ScreenProperties properties) {
    super.parseTraits(traits, properties);

    threeDimensional = TraitParser.parseBoolean(traits, "3d");
  }

  public String startHtml(String indent, Point translation) {
    this.setCommonAttributes();

    if (bgColor != null && !useDisplayBg) {
      styles.put("background-color", bgColor.toColorString());
    }

    if (lineColor != null) {
      float px = 1;

      if (lineWidth != null) {
        px = lineWidth;
      }

      String style = "solid";

      if (dash) {
        style = "dotted";
      }

      styles.put("border", px + "px " + style + " " + lineColor.toColorString());
    }

    if (fill && fillColor != null && bgColor == null) {
      LOGGER.log(Level.INFO, "fill being using in HTML object!");
      styles.put("background-color", fillColor.toColorString());
    }

    int originX = x + translation.x;
    int originY = y + translation.y;

    styles.put("left", originX + "px");
    styles.put("top", originY + "px");

    String attrStr = this.getAttributesString(attributes);
    String classStr = this.getClassString(classes);
    String styleStr = this.getStyleString(styles);

    String html = indent + "<div " + attrStr + " " + classStr + " " + styleStr + ">\n";

    return html;
  }

  public String endHtml(String indent) {
    return indent + "</div>\n";
  }

  @Override
  public String toHtml(String indent, Point translation) {
    String html;

    html = startHtml(indent, translation);
    html = html + endHtml(indent);

    return html;
  }
}
