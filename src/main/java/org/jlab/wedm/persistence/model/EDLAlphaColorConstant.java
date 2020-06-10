package org.jlab.wedm.persistence.model;

/**
 *
 * @author ryans
 */
public class EDLAlphaColorConstant extends EDLColorConstant{
    
    private final int a;
    
    public EDLAlphaColorConstant(int index, String name, int r, int g, int b, int a) {
        super(index, name, r, g, b);
        this.a = a;
    }
    
    public String toRgbaString() {
        return "rgba(" + r + ", " + g + ", " + b + ", " + a + ")";
    }    
    
    @Override
    public String toColorString() {
        return toRgbaString();
    }    
    
}
