package nio_server;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    public static void main(String... args) throws IOException, InterruptedException {
        try (Socket socket = new Socket("localhost", 5050);
             PrintWriter out = new PrintWriter(new BufferedOutputStream(socket.getOutputStream()), true);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            System.out.println("Соединение установлено\n");

            for (int i = 0; i < 5; i++) {
                out.println("test 123 qwerty №" + i);
                System.out.println("Сообщение отправлено...");
                System.out.println("Получено: " + reader.readLine());

                Thread.sleep(133);
            }
        }
    }
}
