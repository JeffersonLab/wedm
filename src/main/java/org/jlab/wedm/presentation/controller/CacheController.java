package org.jlab.wedm.presentation.controller;

import java.io.IOException;
import java.util.Collection;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jlab.wedm.business.service.ScreenService;
import org.jlab.wedm.persistence.model.HtmlScreen;

/**
 *
 * @author slominskir
 */
@WebServlet(name = "Cache", urlPatterns = {"/cache"})
public class CacheController extends HttpServlet {

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Collection<HtmlScreen> screens = ScreenService.SCREEN_CACHE.values();
        
        request.setAttribute("screens", screens);

        request.getRequestDispatcher("/WEB-INF/views/cache.jsp").forward(request, response);
    }
}
