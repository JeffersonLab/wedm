package org.jlab.wedm.persistence.model;

import java.util.Map;

/**
 *
 * @author ryans
 */
public class ColorList {
    private final Map<Integer, EDLColor> indexMap;
    private final Map<String, EDLColor> nameMap;
    private final int rgbDepth;
    private final AlarmColors alarmColors;
    
    public ColorList(Map<Integer, EDLColor> indexMap, Map<String, EDLColor> nameMap, int rgbDepth, AlarmColors alarmColors) {
        this.indexMap = indexMap;
        this.nameMap = nameMap;
        this.rgbDepth = rgbDepth;
        this.alarmColors = alarmColors;
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

    public AlarmColors getAlarmColors() {
        return alarmColors;
    }
}
