package org.jlab.wedm.widget.html;

import java.util.HashMap;
import java.util.Map;
import org.jlab.wedm.persistence.model.HtmlScreen;

/**
 * @author slominskir
 */
public class ActiveMenuButton extends ActiveButton {

  @Override
  public String getButtonFaceHtml(String html, String indent) {
    Map<String, String> menuStyles = new HashMap<>();

    if (topShadowColor != null && botShadowColor != null) {
      menuStyles.put("border-top", "1px solid " + topShadowColor.toColorString());
      menuStyles.put("border-left", "1px solid " + topShadowColor.toColorString());

      menuStyles.put("border-bottom", "1px solid " + botShadowColor.toColorString());
      menuStyles.put("border-right", "1px solid " + botShadowColor.toColorString());
    }

    String styleStr = getStyleString(menuStyles);

    html = html + indent + "<div class=\"menu-button-container\">\n";
    html = super.getButtonFaceHtml(html, indent + HtmlScreen.INDENT_STEP);
    html =
        html
            + indent
            + HtmlScreen.INDENT_STEP
            + "<div class=\"menu-button-box\"><div><div "
            + styleStr
            + "></div></div></div>\n";
    html = html + indent + "</div>\n";

    return html;
  }
}
