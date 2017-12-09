package nio_server.runners;

import nio_server.context.SettingsServerContext;
import nio_server.server.Server;
import nio_server.Sleeper;
import nio_server.context.GlobalContext;


public class ServerStarter {

    private static final String PREFIX = "ServerStarter: ";

    public static void main(String... args) {

        final GlobalContext context = GlobalContext.getInstance();
        context.setServerContext(SettingsServerContext.getInstance());

        Thread server = new Thread(new Server(context.getServerContext().port()));
        server.start();
        System.out.println("Server started");

        Sleeper.sleep(context.getServerContext().lifeTime());

        System.out.println(PREFIX + "Прерывание работы сервера...");
        server.interrupt();
        System.out.println(PREFIX + "Запрос на прерывание работы сервера отправлен.");
    }
}
