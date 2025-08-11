package org.jlab.wedm.widget.html;

import java.awt.Point;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import org.jlab.wedm.persistence.io.EDLParser;
import org.jlab.wedm.persistence.io.Functions;
import org.jlab.wedm.persistence.io.TraitParser;
import org.jlab.wedm.widget.ScreenProperties;

/**
 * @author slominskir
 */
public class ShellCommand extends ActiveButton {

  public int numCommands;
  public String[] commands;

  public String url = null;

  @Override
  public void parseTraits(Map<String, String> traits, ScreenProperties properties) {
    super.parseTraits(traits, properties);

    push =
        true; // Override ActiveButton button type logic - a shell command is always a push button

    numCommands = TraitParser.parseInt(traits, "numCmds", 0);
    commands = TraitParser.parseStringArray(traits, numCommands, "command");

    if (numCommands == 1) {
      String command = commands[0];

      String[] tokens = command.split(" ");

      if (tokens.length == 2) {
        switch (tokens[0]) {
          case "firefox":
          case "chrome":
          case "google-chrome":
          case "chromium":
          case "chromium-browser":
            if (tokens[1] != null && tokens[1].startsWith("http")) {
              url = tokens[1];
            }
            break;
        }
      }

      // Not a browser open command, so let's see if it's an OTF command (if OTF enabled)
      if (url == null && EDLParser.OTF_DIR != null) {

        String filename = EDLParser.OTF_MAP.get(command);

        if (filename != null) {

          String filepath = EDLParser.OTF_DIR + File.separator + filename;

          String prefix = Functions.contextPrefix();

          try {
            url = prefix + "/wedm/screen?edl=" + URLEncoder.encode(filepath, "UTF-8");
          } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8 Unsupported");
          }
        }
      }
    }
  }

  @Override
  protected void setInteractable() {
    if (url != null) {
      classes.add("interactable");
    } else {
      classes.add("disabled-interactable");
    }
  }

  @Override
  public String toHtml(String indent, Point translation) {

    if (url != null) {
      attributes.put("data-url", url);
    }

    return super.toHtml(indent, translation);
  }
}
