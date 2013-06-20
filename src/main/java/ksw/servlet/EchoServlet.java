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

/*
   Test servlet.
   Servlet, which is a get, which returns a form for a post
*/
public class EchoServlet extends HttpServlet
{
    public static final String ServletName = "echo";

    //------------------------------------------------------------
    public void doGet(HttpServletRequest request,
                      HttpServletResponse response)
        throws ServletException, IOException
    {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        ServletHelp.writeDocType(out);
        ServletHelp.writeHeader(out, "Echo entry");

        out.println("<BODY>");
        out.println("<h2>Please enter a word to be echoed<h2><br>");

        out.print("<form action=");
        out.print(ServletServer.getServer().getAddress());
        out.print(EchoReply.ServletName);
        out.println(" method=POST>");
        out.println("Echo: ");
        out.println("<input type=text name=echostring value=xx maxlength=30>");
        out.println("<br>");
        ServletHelp.writeButton(out, "doecho", "Echo");
        out.println("</form>");

        ServletHelp.writeFooter(out);
    }
}
