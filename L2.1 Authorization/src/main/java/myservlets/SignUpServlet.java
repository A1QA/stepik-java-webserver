package myservlets;

import accounts.AccountService;
import accounts.UserProfile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * Use Postman for a testing.
 */

public class SignUpServlet extends HttpServlet {

    private final AccountService accountService;

    public SignUpServlet(AccountService accountService) {
        super();
        this.accountService = accountService;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {


        String login = req.getParameter("login");
        String password = req.getParameter("password");

        resp.setContentType("text/html;charset=utf-8");

        if (login != null && password != null) {
            accountService.addNewUser(new UserProfile(login, password, "unknown"));

            resp.setStatus(HttpServletResponse.SC_OK);
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }

    }
}
