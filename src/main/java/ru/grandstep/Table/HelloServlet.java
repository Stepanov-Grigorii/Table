package ru.grandstep.Table;

import ru.grandstep.Table.beans.StatelessEjbExample;

import java.io.*;
import javax.ejb.EJB;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

@WebServlet(name = "helloServlet", value = "/hello-servlet")
public class HelloServlet extends HttpServlet {
    @EJB
    private StatelessEjbExample statelessEjbExample;


    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");

        // Hello
        PrintWriter out = response.getWriter();
        out.println("<html><body>");
        out.println("<h1>" + statelessEjbExample.hw() + "</h1>");
        out.println("</body></html>");
    }
}