package nio_server.runners;

import nio_server.Server;
import nio_server.Sleeper;

import static nio_server.Settings.SERVER_LIFE_TIME;
import static nio_server.Settings.SERVER_PORT;

public class ServerStarter {

    private static final String PREFIX = "ServerStarter: ";

    public static void main(String... args) {

        Thread server = new Thread(new Server(SERVER_PORT));
        server.start();
        System.out.println("Server started");

        Sleeper.sleep(SERVER_LIFE_TIME);

        System.out.println(PREFIX + "Прерывание работы сервера...");
        server.interrupt();
        System.out.println(PREFIX + "Запрос на прерывание работы сервера отправлен.");
    }
}
