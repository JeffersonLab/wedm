package org.jlab.wedm.persistence.model;

/**
 *
 * @author ryans
 */
public abstract class EDLColor {
    final int index;
    final String name;
    
    public EDLColor(int index, String name) {
        this.index = index;
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }
    
    public abstract String toColorString();
}
