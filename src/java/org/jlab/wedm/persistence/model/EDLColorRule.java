package org.jlab.wedm.persistence.model;

/**
 *
 * @author ryans
 */
public class EDLColorRule extends EDLColor {
    
    private final String expression;
    
    public EDLColorRule(int index, String name, String expression) {
        super(index, name);
        this.expression = expression;
    }

    public String getExpression() {
        return expression;
    }
    
    @Override
    public String toColorString() {
        return String.valueOf(index);
    }
}
