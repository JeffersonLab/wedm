package org.jlab.wedm.persistence.model;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;

/**
 *
 * @author slominskir
 */
public class HtmlScreen {
    
    private static final Logger LOGGER = Logger.getLogger(HtmlScreen.class.getName());    
    
    public static final String DEFAULT_INDENT_STEP = "    ";  
    public static final String DEFAULT_INITIAL_INDENT = "        ";
    public static final String INDENT_STEP;  
    public static final String INITIAL_INDENT;    
    
    static {
        String productionRelease = null;
        
        try {
        Context env = (Context) new InitialContext().lookup("java:comp/env");
        productionRelease = (String) env.lookup("productionRelease");
        } catch(Exception e) {
            LOGGER.log(Level.WARNING, "Unable to read web.xml env: ", e);
        }
        
        if("true".equals(productionRelease)) {
            INDENT_STEP = "";
            INITIAL_INDENT = "";
        } else {
            INDENT_STEP = DEFAULT_INDENT_STEP;
            INITIAL_INDENT = DEFAULT_INITIAL_INDENT;
        }
    }
    
    private final AtomicInteger usageCounter = new AtomicInteger();
    private final long characterCount;
    private float generateSeconds;
    private final long modifiedDate;
    private final String canonicalPath;
    private final String html;
    private final String css;
    private final String title;
    private final String js;
    
    public HtmlScreen(String canonicalPath, long modifiedDate, String html, String css, String js, String title) {
        this.canonicalPath = canonicalPath;
        this.modifiedDate = modifiedDate;
        this.html = html;
        this.css = css;
        this.js = js;
        this.title = title;
        
        characterCount = html.length() + css.length() + js.length() + canonicalPath.length();
    }
    
    public long getModifiedDate() {
        return modifiedDate;
    }
    
    public String getCanonicalPath() {
        return canonicalPath;
    }    

    public String getHtml() {
        return html;
    }

    public String getCss() {
        return css;
    }
    
    public String getJs() {
        return js;
    }
    
    public String getTitle() {
        return title;
    }    

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final HtmlScreen other = (HtmlScreen) obj;
        return Objects.equals(this.canonicalPath, other.canonicalPath);
    }
    
    @Override
    public int hashCode() {
        return canonicalPath.hashCode();
    }        

    public void incrementUsageCount() {
        usageCounter.incrementAndGet();
    }
    
    public int getUsageCount() {
        return usageCounter.get();
    }
    
    public long getCharacterCount() {
        return characterCount;
    }
    
    public void setGenerateSeconds(float generateSeconds) {
        this.generateSeconds = generateSeconds;
    }
    
    public float getGenerateSeconds() {
        return generateSeconds;
    }
}
