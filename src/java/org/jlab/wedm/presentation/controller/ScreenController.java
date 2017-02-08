package org.jlab.wedm.presentation.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jlab.wedm.business.service.ScreenService;
import org.jlab.wedm.persistence.model.Screen;

/**
 *
 * @author ryans
 */
@WebServlet(name = "Screen", urlPatterns = {"/screen"})
public class ScreenController extends HttpServlet {

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
        
        Screen screen = service.load(edlname);
        
        request.setAttribute("screen", screen);
        
        request.getRequestDispatcher("/WEB-INF/views/screen.jsp").forward(request, response);
    }
}
