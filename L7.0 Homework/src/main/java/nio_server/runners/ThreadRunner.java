package nio_server.runners;

public class ThreadRunner {

    private static final String PREFIX = "ThreadRunner: ";

    public static void main(String... args) {

        new Thread(ServerStarter::main).start();

        new Thread(() -> {
            System.out.println(PREFIX + "Запуск клиента");
            ClientStarter.main();
            System.out.println(PREFIX + "Клиент завершил работу");
        }).start();

    }
}
