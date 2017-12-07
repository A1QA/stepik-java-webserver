package nio_server.runners;

import nio_server.Sleeper;
import nio_server.runners.printer.CharsPrinter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ProcessRunner {

    public static final String CLASS_PATH = "\"./L7.0 Homework/target/classes\"";
    private static final String PREFIX = "ProcessRunner: ";

    public static void main(String... args) {

        new Thread(ProcessRunner::runServer).start();
        Sleeper.sleep(15);
        new Thread(ProcessRunner::runClient).start();
    }
    private static void runClient() {
        JavaProcessRunner process = new JavaProcessRunner(JavaProcessRunner.getProcessCommand(CLASS_PATH, ClientStarter.class.getCanonicalName()));
        try {
            System.out.println(PREFIX + "Запуск клиента в новом процессе");
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
        System.out.println(PREFIX + "Процесс клиента завершен");
    }

    private static void runServer() {
        JavaProcessRunner process = new JavaProcessRunner(JavaProcessRunner.getProcessCommand(CLASS_PATH, ServerStarter.class.getCanonicalName()));
        try {
            System.out.println(PREFIX + "Запуск сервера в новом процессе");
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
        System.out.println(PREFIX + "Процесс сервера завершен");
    }
}
