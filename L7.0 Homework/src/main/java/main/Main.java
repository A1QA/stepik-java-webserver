package main;

import server.EchoSocketHandlerCreator;
import server.Server;

import java.io.IOException;

/**
 * Пример из лекций:
 * https://github.com/esin88/MessageSystem
 * Альтернативная лекция:
 * https://youtu.be/sv3nwJHKGS0
 */

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        new Server(new EchoSocketHandlerCreator()).join();
        System.out.println("Main finished");
    }
}
