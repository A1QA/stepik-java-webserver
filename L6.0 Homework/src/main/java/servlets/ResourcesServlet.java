package servlets;

import resourceServer.MBeanHelper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

public class ResourcesServlet extends HttpServlet {

    public static final String PAGE_URL = "/resources";
    private static final Logger logger = Logger.getLogger(ResourcesServlet.class.getName());

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String path = req.getParameter("path");
        logger.info(PAGE_URL + "/" + path);

        MBeanHelper.registerMBean(path);
    }
}
