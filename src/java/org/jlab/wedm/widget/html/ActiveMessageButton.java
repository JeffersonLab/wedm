package org.jlab.wedm.widget.html;

import java.awt.Point;
import java.util.Arrays;

/**
 *
 * @author ryans
 */
public class ActiveMessageButton extends ActiveButton {

    @Override
    public String toHtml(String indent, Point translation) {
        if (push == null) {
            push = true;
        }
        
        // If we are dealing with a "local control"
        if (controlPv != null && controlPv.startsWith("LOC\\")) {
            int index = controlPv.indexOf("=e:");
            if(index > 0) { // And it is of type enum
                String[] tokens = (controlPv.substring(index)).split(",");
                if(tokens.length > 0) { // First part of LOC enum declartion is default value, not labels
                    tokens = Arrays.copyOfRange(tokens, 1, tokens.length);
                }
                // Make sure pressValue and releaseValue use enum index, not label
                if(pressValue != null) {
                    try {
                        Float.parseFloat(pressValue);
                    } catch(NumberFormatException e) {
                        for(int i = 0; i < tokens.length; i++) {
                            if(pressValue.equals(tokens[i])) {
                                pressValue = String.valueOf(i);
                                break;
                            }
                        }
                    }
                }
                if(releaseValue != null) {
                    try {
                        Float.parseFloat(releaseValue);
                    } catch(NumberFormatException e) {
                        for(int i = 0; i < tokens.length; i++) {
                            if(releaseValue.equals(tokens[i])) {
                                releaseValue = String.valueOf(i);
                                break;
                            }
                        }
                    }
                }                
            }
        }
        
        return super.toHtml(indent, translation);
    }
}
