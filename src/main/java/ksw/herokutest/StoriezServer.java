package ksw.herokutest;

import java.io.File;

import ksw.kwutil.simpledb.JsonDb;

public class StoriezServer extends RestAndResourceServer
{
    private JsonDb _db;

    public static final int DefaultPortNumber = 8060;

    public static void main(String[] args)
    {
        try {
            StoriezServer ss = new StoriezServer();
            ss.setup();

            String portS = System.getenv("PORT");
            int portNumber = DefaultPortNumber;
            if (portS != null && portS.length() > 0) {
                portNumber = Integer.valueOf(portS);
            }

            ss.run(portNumber);
        }
        catch (Exception exc) {
            System.err.println("Exception " + exc);
        }
    }
    
    private StoriezServer()
    {
        
    }
    
    private void setup()
    {
        String inEclipseVar = System.getenv("INECLIPSE");
        boolean inEclipse = inEclipseVar != null && inEclipseVar.length() > 0;
        _db = new JsonDb();
        try {
            String dbLocation = inEclipse ? "ksw/herokutest/test.jsd" : "src/main/java/ksw/herokutest/test.jsd";
            _db.read(new File(dbLocation));
        }
        catch(Exception exc) {
            System.err.println("Failure reading: " + exc);
        }
        StorieRequestHandler storieH = new StorieRequestHandler(_db);
        addRestHandler(storieH, "/storiez");
        TextSectionRequestHandler textsH = new TextSectionRequestHandler(_db);
        addRestHandler(textsH, "/textsections");
        TextSectionDispRequestHandler textsvH = new TextSectionDispRequestHandler(_db);
        addRestHandler(textsvH, "/textsectiondisps");
        
        // the content root varies, depending on whether we're in eclipse or not
        String contentPath = null;
        String libPath = null;
        if (inEclipse) {
            contentPath = "ksw/herokutest/content";
            libPath = "website";
        }
        else {
            contentPath = "src/main/webapp";
            libPath = "src/main/webapp";
        }

        addResourceHandler(contentPath);
        addResourceHandler(libPath);
    }
}
