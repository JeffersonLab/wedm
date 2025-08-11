package org.jlab.wedm.persistence.model;

/**
 * @author slominskir
 */
public class EDLFont {

  public String name;
  public boolean bold;
  public boolean italic;
  public float size;

  public EDLFont(String name, boolean bold, boolean italic, float size) {
    this.name = name;
    this.bold = bold;
    this.italic = italic;
    this.size = size;
  }
}
