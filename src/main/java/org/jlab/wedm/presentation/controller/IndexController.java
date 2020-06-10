package org.jlab.wedm.presentation.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jlab.wedm.persistence.io.EDLParser;

/**
 *
 * @author slominskir
 */
@WebServlet(name = "Index", urlPatterns = {"/index"})
public class IndexController extends HttpServlet {

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

        request.setAttribute("edlRootDir", EDLParser.EDL_ROOT_DIR);

        request.getRequestDispatcher("/WEB-INF/views/index.jsp").forward(request, response);
    }
}
