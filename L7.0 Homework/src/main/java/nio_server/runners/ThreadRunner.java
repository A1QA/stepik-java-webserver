package nio_server.runners;

import nio_server.logger.Logger;

public class ThreadRunner {

    private static Logger logger = Logger.getInstance();
    private static final String PREFIX = "ThreadRunner: ";

    public static void main(String... args) {

        new Thread(ServerStarter::main).start();

        new Thread(() -> {
            //System.out.println(PREFIX + "Запуск клиента");
            logger.log(ServerStarter.class, "Запуск клиента");

            ClientStarter.main();
            //System.out.println(PREFIX + "Клиент завершил работу");
            logger.log(ServerStarter.class, "Клиент завершил работу");

        }).start();

    }
}
