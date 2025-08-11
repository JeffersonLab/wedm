package org.jlab.wedm.persistence.model;

/**
 * @author slominskir
 */
public class EDLColorRule extends EDLColor {

  private final String expression;
  private final String firstColor;

  public EDLColorRule(int index, String name, String expression, String firstColor) {
    super(index, name);
    this.expression = expression;
    this.firstColor = firstColor;
  }

  public String getExpression() {
    return expression;
  }

  @Override
  public String toColorString() {
    return String.valueOf(index);
  }

  public String getFirstColor() {
    return firstColor;
  }
}
