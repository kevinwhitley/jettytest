/*
    Copyright (c) 2003 Kevin S. Whitley
    All rights reserved.
*/

package ksw.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
   Test servlet.
   Servlet, which handles a post of an echo request
*/
public class EchoReply extends HttpServlet
{
    public static final String ServletName = "echoreply";
    
    //------------------------------------------------------------
    public void doPost(HttpServletRequest request,
                       HttpServletResponse response)
        throws ServletException, IOException
    {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        ServletParameters params = new ServletParameters(request);

        ServletHelp.writeDocType(out);
        ServletHelp.writeHeader(out, "Echo reply");

        out.println("<BODY>");
        String echos = params.getValueString("echostring", "junk");
        out.println("<h1><u>" + echos + "</u></h1>");

        out.println("<br>");
        out.print("<form action=");
        out.print(ServletServer.getServer().getAddress());
        out.print(EchoServlet.ServletName);
        out.println(" method=GET>");
        ServletHelp.writeButton(out, "doagain", "Again");
        out.println("</form>");

        ServletHelp.writeFooter(out);

        return;
    }
}
