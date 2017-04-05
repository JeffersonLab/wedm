package org.jlab.wedm.presentation.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jlab.wedm.business.service.ScreenService;
import org.jlab.wedm.persistence.model.HtmlScreen;
import org.jlab.wedm.persistence.model.Macro;

/**
 *
 * @author ryans
 */
@WebServlet(name = "Screen", urlPatterns = {"/screen"})
public class ScreenController extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(ScreenController.class.getName());    
    
    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String edlname = request.getParameter("edl");
        
        List<Macro> macros = new ArrayList<>();
        Enumeration e = request.getParameterNames();
        while(e.hasMoreElements()) {
            String name = (String)e.nextElement();
            if(name.startsWith("$(") && name.endsWith(")")) { // We prefix with "$(" to namespace them and avoid collision if someone was to use a macro with name "edl" and also because now it is already in the format needed for search and replace
                String value = request.getParameter(name);
                macros.add(new Macro(name, value));
            }
        }
        
        ScreenService service = new ScreenService();
        
        long start = System.currentTimeMillis();
        HtmlScreen screen = service.load(edlname, macros);
        long end = System.currentTimeMillis();
        
        LOGGER.log(Level.FINEST, "Screen Service Load Time: (seconds) {0}", (end - start) / 1000.0);
        
        request.setAttribute("screen", screen);
        
        request.getRequestDispatcher("/WEB-INF/views/screen.jsp").forward(request, response);
    }
}
