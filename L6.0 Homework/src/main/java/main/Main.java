package main;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import servlets.ResourcesServlet;

import java.util.logging.Logger;

public class Main {

    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) throws Exception {
        logger.fine("App is starting...");

        ResourcesServlet servlet = new ResourcesServlet();

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.addServlet(new ServletHolder(servlet), ResourcesServlet.PAGE_URL);
        Server server = new Server(8080);
        server.setHandler(context);

        server.start();
        logger.info("Server started");

        server.join();
    }


}
