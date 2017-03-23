package org.jlab.wedm.persistence.model;

import java.util.Objects;

/**
 *
 * @author ryans
 */
public class HtmlScreen {
    
    public static final String INDENT_STEP = "    ";  
    
    private final String canonicalPath;
    private final String html;
    private final String css;
    private final String title;
    private final String js;
    
    public HtmlScreen(String canonicalPath, String html, String css, String js, String title) {
        this.canonicalPath = canonicalPath;
        this.html = html;
        this.css = css;
        this.js = js;
        this.title = title;
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
}
