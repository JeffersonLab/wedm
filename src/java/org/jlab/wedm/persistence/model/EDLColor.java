package org.jlab.wedm.persistence.model;

/**
 *
 * @author ryans
 */
public class EDLColor {
    public String name;
    public int r;
    public int g;
    public int b;

    public EDLColor(String name, int r, int g, int b) {
        this.name = name;
        this.r = r;
        this.g = g;
        this.b = b;
    }
    
    public String toRgbString() {
        return "rgb(" + r + ", " + g + ", " + b + ")";
    }    
}
