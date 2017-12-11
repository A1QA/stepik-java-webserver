package nio_server.runners;

import nio_server.Sleeper;
import nio_server.context.GlobalContext;
import nio_server.context.SettingsProcessContext;
import nio_server.logger.Logger;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ProcessRunner {

    private static Logger logger = Logger.getInstance();
    private static final String CLASS_PATH = "\"./L7.0 Homework/target/classes\"";
    private static final String PREFIX = "ProcessRunner: ";

    public static void main(String... args) {

        GlobalContext.getInstance().setProcessContext(SettingsProcessContext.getInstance());

        new Thread(ProcessRunner::runServer).start();
        Sleeper.sleep(1); // на всякий случай, чтобы клиент первым не запустился
        new Thread(ProcessRunner::runClient).start();
    }

    private static void runServer() {
        JavaProcessRunner process = new JavaProcessRunner(JavaProcessRunner.getProcessCommand(CLASS_PATH, ServerStarter.class.getCanonicalName()));
        try {
            //System.out.println(PREFIX + "Запуск сервера в новом процессе");
            logger.log(ProcessRunner.class, "Запуск сервера в новом процессе");
            process.runProcess();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            process.waitFor(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        process.destroy();
        //System.out.println(PREFIX + "Процесс сервера завершен");
        logger.log(ProcessRunner.class, "Процесс сервера завершен");
    }

    private static void runClient() {
        JavaProcessRunner process = new JavaProcessRunner(JavaProcessRunner.getProcessCommand(CLASS_PATH, ClientStarter.class.getCanonicalName()));
        try {
            //System.out.println(PREFIX + "Запуск клиента в новом процессе");
            logger.log(ProcessRunner.class, "Запуск клиента в новом процессе");
            process.runProcess();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            process.waitFor(15, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        process.destroy();
        //System.out.println(PREFIX + "Процесс клиента завершен");
        logger.log(ProcessRunner.class, "Процесс клиента завершен");

    }
}
