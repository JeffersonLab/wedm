package org.jlab.wedm.presentation.controller;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jlab.wedm.business.service.ScreenService;
import org.jlab.wedm.persistence.model.HtmlScreen;

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
        
        ScreenService service = new ScreenService();
        
        long start = System.currentTimeMillis();
        HtmlScreen screen = service.load(edlname);
        long end = System.currentTimeMillis();
        
        LOGGER.log(Level.FINEST, "Screen Service Load Time: (seconds) {0}", (end - start) / 1000.0);
        
        request.setAttribute("screen", screen);
        
        request.getRequestDispatcher("/WEB-INF/views/screen.jsp").forward(request, response);
    }
}
