package org.jlab.wedm.widget.html;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import org.jlab.wedm.persistence.model.EDLColor;

/**
 *
 * @author ryans
 */
public class ActiveMotifSlider extends HtmlScreenObject {

    public EDLColor secondBgColor;
    public Float scaleMin = null;
    public Float scaleMax = null;
    public String controlLabel = null;
    public boolean showLimits = false;
    public boolean showLabel = false;

    protected Map<String, String> trackStyles = new HashMap<>();
    protected Map<String, String> handleStyles = new HashMap<>();

    @Override
    public String toHtml(String indent, String indentStep, Point translation) {
        String html;

        if (orientation == null) {
            orientation = "horizontal";
        }

        if (scaleMin != null) {
            attributes.put("data-min", String.valueOf(scaleMin));
        }

        if (scaleMax != null) {
            attributes.put("data-max", String.valueOf(scaleMax));
        }

        classes.add("MouseSensitive");

        if (secondBgColor != null) {
            trackStyles.put("background-color", secondBgColor.toColorString());
        }

        trackStyles.put("position", "absolute");
        trackStyles.put("top", "3px");
        trackStyles.put("bottom", "3px");
        trackStyles.put("left", "3px");
        trackStyles.put("right", "3px");
        
        if (topShadowColor != null && botShadowColor != null) {
            trackStyles.put("border-bottom", "2px solid " + topShadowColor.toColorString());
            trackStyles.put("border-right", "2px solid " + topShadowColor.toColorString());
            trackStyles.put("border-top", "2px solid " + botShadowColor.toColorString());
            trackStyles.put("border-left", "2px solid " + botShadowColor.toColorString());

            handleStyles.put("border-top", "2px solid " + topShadowColor.toColorString());
            handleStyles.put("border-left", "2px solid " + topShadowColor.toColorString());
            handleStyles.put("border-bottom", "2px solid " + botShadowColor.toColorString());
            handleStyles.put("border-right", "2px solid " + botShadowColor.toColorString());
        }

        if(bgColor != null) {
            handleStyles.put("background-color", bgColor.toColorString());
        }
        
        String leftHandleStyleStr;
        String rightHandleStyleStr;
        
        final float handleSize = 15.0f;

        if ("horizontal".equals(orientation)) {
            float handleWidth = handleSize;
            
            handleStyles.put("width", String.valueOf(handleWidth) + "px");
            handleStyles.put("height", String.valueOf(this.h - 10) + "px");

            handleStyles.put("left", "0");
            handleStyles.put("border-right-width", "1px");
            leftHandleStyleStr = getStyleString(handleStyles);

            handleStyles.put("left", handleWidth + "px");
            handleStyles.put("border-left-width", "1px");
            handleStyles.put("border-right-width", "2px");            
            rightHandleStyleStr = getStyleString(handleStyles);
            
            trackStyles.put("padding", "0 " + handleWidth + "px");
        } else { // vertical
            float handleHeight = handleSize;

            handleStyles.put("width", String.valueOf(this.w - 10) + "px");
            handleStyles.put("height", String.valueOf(handleHeight) + "px");

            handleStyles.put("top", "0");
            handleStyles.put("border-bottom-width", "1px !important");          
            leftHandleStyleStr = getStyleString(handleStyles);

            handleStyles.put("top", handleHeight + "px");
            handleStyles.put("border-top-width", "1px");
            handleStyles.put("border-bottom-width", "2px");            
            rightHandleStyleStr = getStyleString(handleStyles);
            
            trackStyles.put("padding", handleHeight + "px 0");            
        }

        String trackStyleStr = getStyleString(trackStyles);

        html = startHtml(indent, indentStep, translation);
        html = html + indent + "<div class=\"slider-track\" " + trackStyleStr + ">";
        html = html + indent + indentStep + "<div class=\"knob\">\n";
        html = html + indent + indentStep + indentStep + "<div class=\"knob-handle\" "
                + leftHandleStyleStr + "></div>";
        html = html + indent + indentStep + indentStep + "<div class=\"knob-handle\" "
                + rightHandleStyleStr + "></div>";
        html = html + indent + indentStep + "</div>\n";
        html = html + indent + "</div>\n";
        html = html + endHtml(indent, indentStep);

        return html;
    }
}
