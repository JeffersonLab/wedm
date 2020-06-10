package org.jlab.wedm.lifecycle;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 *
 * @author slominskir
 */
@WebListener
public class Configuration implements ServletContextListener {

    private static final Logger LOGGER = Logger.getLogger(Configuration.class.getName());

    public static final Properties PROPERTIES = new Properties();
    public static final Map<String, String> CLASS_MAP = new HashMap<>();
    public static final List<String> WIDGET_LIST = new ArrayList<>();

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream in = classLoader.getResourceAsStream("wedm.properties");
        try {
            PROPERTIES.load(in);

            for (Object ko : PROPERTIES.keySet()) {
                String edmClassName = (String) ko;
                String javaClassName = PROPERTIES.getProperty(edmClassName);
                CLASS_MAP.put(edmClassName, javaClassName);
                WIDGET_LIST.add(edmClassName);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unable to load application properties file", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }

}
