package servlets;

import accountServer.AccountServerI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AdminServlet extends HttpServlet {

    public static final String PAGE_URL = "/admin";
    private static final Logger logger = LogManager.getLogger(AdminServlet.class.getName());
    private final AccountServerI accountServer;

    public AdminServlet(AccountServerI accountServer) {
        this.accountServer = accountServer;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=utf-8");

        int usersLimit = accountServer.getUsersLimit();
        logger.info("{} usersLimit : {}", PAGE_URL, usersLimit);
        resp.getWriter().println(usersLimit);
        resp.setStatus(HttpServletResponse.SC_OK);

    }
}
