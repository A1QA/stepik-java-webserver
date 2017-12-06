package nio_server;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static nio_server.Settings.RUN_CLIENT_IN_NEW_PROCESS;
import static nio_server.Settings.SERVER_LIFE_TIME;
import static nio_server.Settings.SERVER_PORT;

public class Runner {

    private static final String PREFIX = "Runner: ";

    //todo создать сервер, клиента, процесс раннер
    public static void main(String... args) {
        System.out.println("Server started");

        runClient(RUN_CLIENT_IN_NEW_PROCESS);

        Thread server = new Thread(new Server(SERVER_PORT));
        server.start();

        Sleeper.sleep(SERVER_LIFE_TIME);
        System.out.println(PREFIX + "Прерывание работы сервера...");
        server.interrupt();
        System.out.println(PREFIX + "Запрос на прерывание работы сервера отправлен.");
    }


    /**
     * Запуск клиента в отдельном потоке
     *
     * @param inNewProcess есди true, запускает в отдельном процессе.
     */
    private static void runClient(boolean inNewProcess) {

        Thread client = new Thread(() -> {
            Sleeper.sleep(5);
            if (!inNewProcess) {
                try {
                    System.out.println(PREFIX + "Запуск клиента");
                    Client.main();
                    System.out.println(PREFIX + "Клиент завершил работу");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                JavaProcessRunner process = new JavaProcessRunner(JavaProcessRunner.getProcessCommand(JavaProcessRunner.CLASS_PATH, JavaProcessRunner.CLASS));
                try {
                    System.out.println(PREFIX + "Запуск клиента в новом процессе");
                    process.runProcess();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //Sleeper.sleep(20000);
                try {
                    process.waitFor(20, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                process.destroy();
                System.out.println(PREFIX + "Процесс клиента завершен");
            }
        });
        client.start();
    }
}
