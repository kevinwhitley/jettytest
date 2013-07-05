package ksw.herokutest;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;

public class RestAndResourceServer
{
    private List<Handler> _handlerList;
    
    public RestAndResourceServer()
    {
        _handlerList = new ArrayList<Handler>(40);
    }
    
    public void addResourceHandler(String path)
    {
        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setResourceBase(path);

        _handlerList.add(resourceHandler);
    }
    
    public void addRestHandler(RestHandler rh, String context)
    {
        ContextHandler restContext = new ContextHandler();
        restContext.setContextPath(context);
        restContext.setHandler(rh);
        
        _handlerList.add(restContext);
    }

    public void run(int portNumber) throws Exception
    {
        Server server = new Server(portNumber);
     
        Handler[] handlers = new Handler[_handlerList.size()];
        _handlerList.toArray(handlers);
        _handlerList = null;  // indicates that we've started the server

        HandlerList hlist = new HandlerList();
        hlist.setHandlers(handlers);
        server.setHandler(hlist);
        
        server.start();
        server.join();
    }
    

}
