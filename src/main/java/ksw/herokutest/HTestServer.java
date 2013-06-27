package ksw.herokutest;

import java.io.File;

import ksw.servlet.FileReadServlet;
import ksw.webserver.WebServer;

public class HTestServer
{
    private WebServer _webserver;
    
    public static final int DefaultPortNumber = 8060;

    public static void main(String[] args)
    {
        HTestServer server = new HTestServer();
        server.run();
    }
    
    private HTestServer()
    {
        /*
        Map<String, Object> serverArguments = new HashMap<String, Object>(1);
        String[] fileServletArg = {"aa", "bb"};
        List<String[]> fileServlets = new ArrayList<String[]>(3);
        fileServlets.add(fileServletArg);
        serverArguments.put(WebServer.FilesArg, fileServlets);
        */
        
        String portS = System.getenv("PORT");
        int portNumber = DefaultPortNumber;
        if (portS != null && portS.length() > 0) {
            portNumber = Integer.valueOf(portS);
        }

        _webserver = new WebServer();
        _webserver.initialize(null, portNumber, null, null);
        
        // the content root varies, depending on whether we're in eclipse or not
        String inEclipseVar = System.getenv("INECLIPSE");
        String contentPath = null;
        String libPath = null;
        if (inEclipseVar != null && inEclipseVar.length() > 0) {
            contentPath = "ksw/herokutest/content";
            libPath = "website/cl";
        }
        else {
            contentPath = "src/main/webapp";
            libPath = "src/main/webapp/cl";
        }

        File contentRoot = new File(contentPath);
        FileReadServlet fs = new FileReadServlet(contentRoot);
        _webserver.addServlet(fs, null);
        FileReadServlet fsLib = new FileReadServlet(new File(libPath));
        _webserver.addServlet(fsLib, "cl");
    }
    
    private void run()
    {
        _webserver.run();
    }
}
