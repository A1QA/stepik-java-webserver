package main;

import server.EchoSocketHandlerCreator;
import server.Server;

import java.io.IOException;

/**
 * Ссылка на задание:
 * https://stepik.org/lesson/13019/step/9
 *
 * Пример из лекций:
 * https://github.com/esin88/MessageSystem
 * Альтернативная лекция:
 * https://youtu.be/sv3nwJHKGS0
 */

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {

        startThreadBasedEchoServer();
        //startNIOBasedEchoServer();

        System.out.println("Main finished");
    }

    /**
     * Вариант через потоки
     */
    private static void startThreadBasedEchoServer() throws IOException, InterruptedException {
        new Server(new EchoSocketHandlerCreator()).join();
    }

    /**
     * Вариант с использованием NIO
     *
     * Просто эхо-сервер, не парсит входящие сообщения на наличие строки "Bye." todo
     * В принципе, для задания достаточно автоматического определения, когда клиент сам закрыл сокет.
     */
    private static void startNIOBasedEchoServer() {
        new nio_server.Server(5050).run();
    }
}
