package ksw.webserver;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

import javax.servlet.Servlet;

import ksw.kwutil.JSONWriter;
import ksw.servlet.AppServlet;
import ksw.servlet.FileReadServlet;
import ksw.servlet.ServletServer;

public class WebServer extends AppServlet.Application
{
    public static final String AppServletName = "ws";

    private ServletServer _sserver;

    // argument tokens
    public static final String FilesArg = "files";


    public void initialize(Map<String, Object> arguments, int portNumber, String appServletName, AppServlet appS)
    {
        _sserver = ServletServer.getServer();
        _sserver.init(portNumber, "");
        // add test servlets
        _sserver.addTestServlets();

        if (arguments != null) {
            // add file-reading servlets - hands back requested files
            List<String[]> fileServlets = (List<String[]>)arguments.get(FilesArg);
            for (String[] servletInfo : fileServlets) {
                _sserver.addServlet(new FileReadServlet(new File(servletInfo[1])), servletInfo[0]);
                System.out.println("use " + servletInfo[0] + " to access files in " + servletInfo[1]);
            }
        }

        if (appServletName != null && appS != null) {
            _sserver.addServlet(appS, appServletName);
        }
    }
    
    // possibly add additional servlets
    // must be called before webserver is started
    public void addServlet(Servlet servlet, String servletName)
    {
        _sserver.addServlet(servlet, servletName);
    }

    public void run()
    {
        // display the local host address, for clients to connect
        try {
            InetAddress localH = InetAddress.getLocalHost();
            System.out.println("Local host is " + localH.toString());
        }
        catch (UnknownHostException uhe) {
            System.out.println("Unknown host?");
        }

        runWebServer();
    }
    
    // standard JSON success response
    public static String makeSuccessResponse(String msg)
    {
        JSONWriter writer = new JSONWriter();
        writer.startObject();
        writer.addItem("action", "success");
        writer.addItem("message", msg);
        writer.endObject();
        return writer.toString();
    }

    public static String makeFailureResponse(String msg)
    {
        JSONWriter writer = new JSONWriter();
        writer.startObject();
        writer.addItem("action", "error");
        writer.addItem("message", msg);
        writer.endObject();
        return writer.toString();
    }

    private void runWebServer ()
    {
        // start the webserver thread
        _sserver.run();
    }

}
