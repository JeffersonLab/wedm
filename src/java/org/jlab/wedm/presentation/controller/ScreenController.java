package org.jlab.wedm.presentation.controller;

import java.io.FileNotFoundException;
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
import org.jlab.wedm.lifecycle.Configuration;
import org.jlab.wedm.persistence.model.HtmlScreen;
import org.jlab.wedm.persistence.model.Macro;

/**
 *
 * @author slominskir
 */
@WebServlet(name = "Screen", urlPatterns = {"/screen"})
public class ScreenController extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(ScreenController.class.getName());

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

        String edlname = request.getParameter("edl");

        List<Macro> macros = new ArrayList<>();
        Enumeration e = request.getParameterNames();
        while (e.hasMoreElements()) {
            String name = (String) e.nextElement();
            // We prefix with "$(" to namespace them and avoid collision if someone was to use 
            // a macro with name "edl" and also because now it is already in the format 
            // needed for search and replace
            if (name.startsWith("$(") && name.endsWith(")")) {
                String value = request.getParameter(name);
                // Turn "$(  XX )" into "$(X)"
                String trimmed_name = name.substring(2, name.length()-1).trim();
                macros.add(new Macro("$(" + trimmed_name + ")", value));
            }
        }

        StringBuilder builder = new StringBuilder();
        if (!macros.isEmpty()) {
            Macro m = macros.get(0);
            builder.append(m.key.substring(2, m.key.length() - 1));
            builder.append("=");
            builder.append(m.value);

            for (int i = 1; i < macros.size(); i++) {
                m = macros.get(i);
                builder.append(",");
                builder.append(m.key.substring(2, m.key.length() - 1));
                builder.append("=");
                builder.append(m.value);
            }
        }

        String macroString = builder.toString();

        ScreenService service = new ScreenService();

        try {
            long start = System.currentTimeMillis();
            HtmlScreen screen = service.load(edlname, macros);
            long end = System.currentTimeMillis();

            LOGGER.log(Level.FINEST, "Screen Service Load Time: (seconds) {0}", (end - start) / 1000.0);

            request.setAttribute("widgets", Configuration.WIDGET_LIST);
            request.setAttribute("screen", screen);
            request.setAttribute("macroString", macroString);

            request.getRequestDispatcher("/WEB-INF/views/screen.jsp").forward(request, response);
        } catch (FileNotFoundException ex) {

            request.setAttribute("edlname", edlname);

            request.getRequestDispatcher("/WEB-INF/views/file-not-found.jsp").forward(request, response);
        }
    }
}
