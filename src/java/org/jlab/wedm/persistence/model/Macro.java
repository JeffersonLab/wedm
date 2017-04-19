package org.jlab.wedm.persistence.model;

/**
 * 
 * @author ryans
 */
public class Macro {
    public String key;
    public String value;

    public Macro(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
