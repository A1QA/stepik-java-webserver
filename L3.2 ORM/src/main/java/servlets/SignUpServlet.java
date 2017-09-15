package servlets;

import dbService.AccountService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SignUpServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String login = req.getParameter("login");
        String password = req.getParameter("password");

        boolean result = AccountService.signUp(login, password);


        resp.setContentType("text/html;charset=utf-8");
        if (result) {
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().println("Registered");
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().println("Already Registered");
        }
    }
}
