package org.jlab.wedm.persistence.model;

/**
 *
 * @author slominskir
 */
public class EDLColorConstant extends EDLColor {
    public int r;
    public int g;
    public int b;

    public EDLColorConstant(int index, String name, int r, int g, int b) {
        super(index, name);
        
        this.r = r;
        this.g = g;
        this.b = b;
    }
    
    public String toRgbString() {
        return "rgb(" + r + ", " + g + ", " + b + ")";
    }    
    
    @Override
    public String toColorString() {
        return toRgbString();
    }
}
