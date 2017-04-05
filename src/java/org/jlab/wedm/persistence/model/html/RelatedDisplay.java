package org.jlab.wedm.persistence.model.html;

import java.awt.Point;

/**
 *
 * @author ryans
 */
public class RelatedDisplay extends ActiveButton {

    @Override
    public String toHtml(String indent, String indentStep, Point translation) {

        String files = "";
        String labels = "";

        if (numDsps > 0 && numDsps <= 64) {
            for (int i = 0; i < displayFileNames.length; i++) {
                if (displayFileNames[i] != null) {
                    //files = files + " " + displayFileNames[i];
                    attributes.put("data-linked-file-" + i, displayFileNames[i]);

                    if (menuLabels[i] != null) {
                        //labels = labels + " " + menuLabels[i];
                        attributes.put("data-linked-label-" + i, menuLabels[i]);
                    } else {
                        //labels = labels + " ~~NONE~~";
                        attributes.put("data-linked-label-" + i, "");
                    }
                    
                    
                    if(symbols[i] != null) {
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
