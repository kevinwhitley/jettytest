package ksw.herokutest;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        String kevinVar = System.getenv("INECLIPSE");
        String rootPath = null;
        if (kevinVar != null && kevinVar.length() > 0) {
            rootPath = "ksw/herokutest/content";
        }
        else {
            rootPath = "src/main/webapp";
        }

        File contentRoot = new File(rootPath);
        FileReadServlet fs = new FileReadServlet(contentRoot);
        _webserver.addServlet(fs, "xx");
    }
    
    private void run()
    {
        _webserver.run();
    }
}
