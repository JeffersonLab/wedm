package org.jlab.wedm.business.service;

import java.io.FileNotFoundException;
import org.jlab.wedm.persistence.io.ColorListParser;
import org.jlab.wedm.persistence.io.ScreenParser;
import org.jlab.wedm.persistence.model.ColorList;
import org.jlab.wedm.persistence.model.Screen;

/**
 *
 * @author ryans
 */
public class ScreenService {

    private ColorList colorList;
    
    public ScreenService() throws FileNotFoundException {
        String colorfile = "colors.list";
        loadColorFile(colorfile);
    }
    
    public Screen load(String name) throws FileNotFoundException {
        
        ScreenParser parser = new ScreenParser();
        
        return parser.parse(name, colorList, false);
    }
    
    private void loadColorFile(String colorfile) throws FileNotFoundException {
        ColorListParser parser = new ColorListParser();
        
        colorList = parser.parse(colorfile);
    }
    
}
