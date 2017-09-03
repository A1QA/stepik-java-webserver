package myservlets;

import accounts.AccountService;
import accounts.UserProfile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SignInServlet extends HttpServlet {

    private final AccountService accountService;

    public SignInServlet(AccountService accountService) {
        super();
        this.accountService = accountService;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String login = req.getParameter("login");
        String password = req.getParameter("password");

        resp.setContentType("text/html;charset=utf-8");

        UserProfile userByLogin = accountService.getUserByLogin(login);
        if (userByLogin != null) {
            if (userByLogin.getLogin().equals(login) && userByLogin.getPass().equals(password)) {
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().print("Authorized: " + login);
            } else {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().print("Unauthorized");
            }
        }

    }
}
