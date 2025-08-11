package org.jlab.wedm.widget.html;

import java.awt.Point;
import java.util.Map;
import org.jlab.wedm.persistence.io.TraitParser;
import org.jlab.wedm.widget.ScreenProperties;

/**
 * @author slominskir
 */
public class RelatedDisplay extends ActiveButton {

  String[] propagateMacros;

  @Override
  public void parseTraits(Map<String, String> traits, ScreenProperties properties) {
    super.parseTraits(traits, properties);

    propagateMacros = TraitParser.parseStringArray(traits, numDsps, "propagateMacros");
  }

  @Override
  protected void setActionValues() {
    // We want to override and set none
  }

  @Override
  protected void setButtonType() {
    // We want to override and set none
  }

  @Override
  protected void setInteractable() {
    if (numDsps > 0) {
      classes.add("interactable");
    }
  }

  @Override
  public String toHtml(String indent, Point translation) {

    if (swapButtons) {
      classes.add("swapped-buttons");
    }

    String files = "";
    String labels = "";

    if (numDsps > 0 && numDsps <= TraitParser.MAX_ARRAY_SIZE) {
      for (int i = 0; i < displayFileName.length; i++) {
        if (displayFileName[i] != null) {
          // files = files + " " + displayFileNames[i];
          attributes.put("data-linked-file-" + i, displayFileName[i]);

          if (menuLabel != null && menuLabel[i] != null) {
            // labels = labels + " " + menuLabels[i];
            attributes.put("data-linked-label-" + i, menuLabel[i]);
          } else {
            // labels = labels + " ~~NONE~~";
            attributes.put("data-linked-label-" + i, "");
          }

          if (symbols != null && symbols[i] != null) {
            attributes.put("data-symbols-" + i, symbols[i]);
          }

          if (propagateMacros != null && "0".equals(propagateMacros[i])) {
            attributes.put("data-propagate-" + i, "false");
          }
        }
      }

      // attributes.put("data-linked-files", files);
      // attributes.put("data-linked-labels", labels);
    }

    return super.toHtml(indent, translation);
  }
}
