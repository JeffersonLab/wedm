package org.jlab.wedm.widget;

import java.util.Map;
import org.jlab.wedm.persistence.model.Screen;

/**
 * @author slominskir
 */
public class EmbeddedScreen extends ScreenProperties {

  public Screen screen;
  public String file;
  public String displaySource;

  @Override
  public void parseTraits(Map<String, String> traits, ScreenProperties properties) {
    super.parseTraits(traits, properties);

    file = traits.get("file");
    displaySource = traits.get("displaySource");
  }
}
