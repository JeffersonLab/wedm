package org.jlab.wedm.persistence.model;

import java.util.List;
import java.util.Map;

/**
 *
 * @author ryans
 */
public class ColorList {

    private final Map<Integer, EDLColor> indexMap;
    private final Map<String, EDLColor> nameMap;
    private final int rgbDepth;
    private final List<EDLColorConstant> staticColors;
    private final AlarmColors alarmColors;
    private final List<EDLColorRule> ruleColors;

    public ColorList(Map<Integer, EDLColor> indexMap, Map<String, EDLColor> nameMap, int rgbDepth,
            List<EDLColorConstant> staticColors, AlarmColors alarmColors,
            List<EDLColorRule> ruleColors) {
        this.indexMap = indexMap;
        this.nameMap = nameMap;
        this.rgbDepth = rgbDepth;
        this.staticColors = staticColors;
        this.alarmColors = alarmColors;
        this.ruleColors = ruleColors;
    }

    public EDLColor lookup(int index) {
        return indexMap.get(index);
    }

    public EDLColor lookup(String name) {
        return nameMap.get(name);
    }

    public int getRgbDepth() {
        return rgbDepth;
    }

    public List<EDLColorConstant> getStaticColors() {
        return staticColors;
    }

    public AlarmColors getAlarmColors() {
        return alarmColors;
    }
    
    public List<EDLColorRule> getRuleColors() {
        return ruleColors;
    }    
}
