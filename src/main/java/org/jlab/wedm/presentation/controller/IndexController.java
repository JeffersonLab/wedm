package org.jlab.wedm.presentation.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.jlab.wedm.persistence.io.EDLParser;

/**
 * @author slominskir
 */
@WebServlet(
    name = "Index",
    urlPatterns = {"/index"})
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
