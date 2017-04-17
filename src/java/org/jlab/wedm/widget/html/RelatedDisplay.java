package org.jlab.wedm.widget.html;

import java.awt.Point;
import org.jlab.wedm.persistence.io.TraitParser;

/**
 *
 * @author ryans
 */
public class RelatedDisplay extends ActiveButton {

    @Override
    public String toHtml(String indent, String indentStep, Point translation) {

        if(swapButtons) {
            classes.add("swapped-buttons");
        }
        
        String files = "";
        String labels = "";
        
        if (numDsps > 0 && numDsps <= TraitParser.MAX_ARRAY_SIZE) {
            for (int i = 0; i < displayFileName.length; i++) {
                if (displayFileName[i] != null) {
                    //files = files + " " + displayFileNames[i];
                    attributes.put("data-linked-file-" + i, displayFileName[i]);

                    if (menuLabel != null && menuLabel[i] != null) {
                        //labels = labels + " " + menuLabels[i];
                        attributes.put("data-linked-label-" + i, menuLabel[i]);
                    } else {
                        //labels = labels + " ~~NONE~~";
                        attributes.put("data-linked-label-" + i, "");
                    }
                    
                    
                    if(symbols != null && symbols[i] != null) {
                        attributes.put("data-symbols-" + i, symbols[i]);
                    }
                }
            }

            //attributes.put("data-linked-files", files);
            //attributes.put("data-linked-labels", labels);
        }

        return super.toHtml(indent, indentStep, translation);
    }
}
