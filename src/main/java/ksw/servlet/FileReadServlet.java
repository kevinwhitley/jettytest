package ksw.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServlet;
import java.io.File;
import java.io.IOException;

/**
 */
public class FileReadServlet extends HttpServlet
{
    public static final String ServletName = "file";

    private File _fileDir;

    public FileReadServlet (File fileDir)
    {
        _fileDir = fileDir;
    }

    protected void doGet (HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.charAt(0) != '/') {
            // can't find it
            response.sendError(404);
            return;
        }

        File readFile = new File(_fileDir, pathInfo);
        ServletHelp.writeFileToResponse(readFile, response);
    }
}
