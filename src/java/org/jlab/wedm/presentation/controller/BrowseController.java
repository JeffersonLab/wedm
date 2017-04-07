package org.jlab.wedm.presentation.controller;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Arrays;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jlab.wedm.persistence.io.EDMParser;

/**
 *
 * @author ryans
 */
@WebServlet(name = "Browse", urlPatterns = {"/browse"})
public class BrowseController extends HttpServlet {

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

        String dirname = request.getParameter("dir");
        boolean parentOutside = true;
        
        if (dirname != null) {
            File dir = new File(dirname);

            if (!dir.getCanonicalPath().startsWith(EDMParser.EDL_ROOT_DIR)) {
                throw new ServletException("Illegal Path");
            }

            String parent = dirname;

            if (dir.getParentFile() != null) {
                parent = dir.getParentFile().getAbsolutePath();

                if (dir.getParentFile().getCanonicalPath().startsWith(EDMParser.EDL_ROOT_DIR)) {
                    parentOutside = false;
                }
            }

            File[] files = dir.listFiles(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    if(f.isDirectory() || f.getName().endsWith(".edl")) {
                        return true;
                    }
                    
                    return false;
                }
            });

            if(files != null) {
                Arrays.sort(files);
            }
            
            request.setAttribute("files", files);
            request.setAttribute("parent", parent);
            request.setAttribute("parentOutside", parentOutside);
        }

        request.getRequestDispatcher("/WEB-INF/views/browse.jsp").forward(request, response);
    }
}
