package org.jlab.wedm.widget.html;

import java.awt.Point;
import java.io.File;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jlab.wedm.persistence.io.EDLParser;
import org.jlab.wedm.persistence.io.IOUtil;
import org.jlab.wedm.persistence.model.ColorPalette;
import org.jlab.wedm.persistence.model.HtmlScreen;

/**
 *
 * @author ryans
 */
public class ActiveImage extends HtmlScreenObject {

    private static final Logger LOGGER = Logger.getLogger(HtmlScreenObject.class.getName());

    public String file;

    @Override
    public void parseTraits(Map<String, String> traits, ColorPalette palette) {
        super.parseTraits(traits, palette);
        
        file = traits.get("file");
    }
    
    @Override
    public String toHtml(String indent, Point translation) {

        String imgHtml;

        // TODO: it would be faster to load pages if we allowed the browser to fetch images asyncronously
        try {
            file = EDLParser.rewriteFileName(file);
            
            File path = new File(file);

            if (!path.isAbsolute()) {
                path = new File(EDLParser.EDL_ROOT_DIR + File.separator + file);
            }

            String type = null;
            
            if(path.getName().endsWith(".gif")) {
                type = "gif";
            } else if(path.getName().endsWith(".png")) {
                type = "png";
            }
            
            if(type == null) {
                throw new RuntimeException("Unsupported image type: " + path.getName());
            }
            
            String contents = IOUtil.encodeBase64(IOUtil.fileToBytes(path));
            
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
