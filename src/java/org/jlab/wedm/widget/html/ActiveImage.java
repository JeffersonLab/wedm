package org.jlab.wedm.widget.html;

import java.awt.Point;
import java.io.File;
import java.net.URL;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jlab.wedm.persistence.io.EDLParser;
import org.jlab.wedm.persistence.io.IOUtil;
import org.jlab.wedm.persistence.model.ColorPalette;
import org.jlab.wedm.persistence.model.HtmlScreen;
import org.jlab.wedm.widget.ScreenProperties;

/**
 *
 * @author slominskir
 */
public class ActiveImage extends HtmlScreenObject {

    private static final Logger LOGGER = Logger.getLogger(HtmlScreenObject.class.getName());

    public String file;

    @Override
    public void parseTraits(Map<String, String> traits, ScreenProperties properties) {
        super.parseTraits(traits, properties);
        
        file = traits.get("file");
    }
    
    @Override
    public String toHtml(String indent, Point translation) {

        String imgHtml;

        // TODO: it would be faster to load pages if we allowed the browser to fetch images asyncronously
        try {
            String type = null;
            
            if(file.contains(".gif")) {
                type = "gif";
            } else if(file.contains(".png")) {
                type = "png";
            }
            
            if(type == null) {
                throw new RuntimeException("Unsupported image type: " + file);
            }

            String contents;
            // Check local file
            File path = new File(file);
            if (!path.isAbsolute()) {
                path = new File(EDLParser.EDL_ROOT_DIR + File.separator + file);
            }
            if (path.canRead()) {
                contents = IOUtil.encodeBase64(IOUtil.fileToBytes(path));
            }
            else {
                // Check URL, using search path etc.
                final URL url = EDLParser.getURL(EDLParser.rewriteFileName(file), false);
                if (url == null)
                    throw new Exception("Cannot locate image " + file);
                contents = IOUtil.encodeBase64(IOUtil.streamToBytes(url.openStream()));
            }
            
            imgHtml = "<img width=\"100%\" height=\"100%\" src=\"data:image/" + type + ";base64," + contents + "\"></img>\n";

        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Bad image file: " + file, e);
            imgHtml = "";
        }

        String html = startHtml(indent, translation);
        html = html + indent + HtmlScreen.INDENT_STEP + imgHtml;
        html = html + endHtml(indent);

        return html;
    }
}
