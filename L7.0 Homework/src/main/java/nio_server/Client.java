package nio_server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.Socket;

import static nio_server.Settings.CLIENT_ITERATION;
import static nio_server.Settings.CLIENT_PAUSE_BEFORE_ITERATION;
import static nio_server.Settings.CLIENT_PAUSE_AFTER_ITERATION;
import static nio_server.Settings.CLIENT_PRINT_WRITER;
import static nio_server.Settings.CLIENT_MESSAGE;



public class Client {
    public static void main(String... args) throws IOException, InterruptedException {
        try (Socket socket = new Socket("localhost", 5050)) {

            if (CLIENT_PRINT_WRITER) {
                withPrintWriter(socket);
            } else {
                //withBufferedWriter(socket);
                withWriter(socket);
            }
        }
    }
    // todo сделать каждый метод реализацией класса, которую использует клиент
    // todo собирать обработчики из списка _команд_


    private static void withWriter(Socket socket) throws InterruptedException, IOException {

        try (Writer out = new OutputStreamWriter(socket.getOutputStream());
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            System.out.println("---Соединение установлено\n");

            for (int i = 0; i < CLIENT_ITERATION; i++) {

/*                try {
                    System.out.println("---Чтение: " + reader.readLine());
                } catch (IOException e) {
                    e.printStackTrace();
                }*/

                System.out.println("---Подготовка к отправке... "+i);
                Thread.sleep(CLIENT_PAUSE_BEFORE_ITERATION);

                try {
                    out.write(CLIENT_MESSAGE + i);
                    out.write("\n"); // reader.readLine() ожидает \n
                    out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.print("---Сообщение отправлено... "+i+"\n");

                Thread.sleep(350);
                try {
                    System.err.println("---Чтение: ");
//                  System.out.println("---" + reader.readLine()); // Простой способ читать _строку_

                    // либо так:
                    System.err.print("---:");
                    int ch = reader.read();
                    while (ch != -1) {
                        System.err.print((char)ch);
                        if (ch == '\n') {
                            break;
                        }
                        ch = reader.read();
                    }

                    System.err.println("---Прочитано");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Thread.sleep(CLIENT_PAUSE_AFTER_ITERATION);
            }

        }
    }


    private static void withBufferedWriter(Socket socket) throws InterruptedException, IOException {

        try (BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            System.out.println("---Соединение установлено\n");

            for (int i = 0; i < CLIENT_ITERATION; i++) {

                System.out.println("---Подготовка к отправке...");
                out.write(CLIENT_MESSAGE + "\n" + i); // reader.readLine() ожидает \n
                out.flush();
                System.out.println("---Сообщение отправлено...");

                //Thread.sleep(333);
                System.out.println("---Получено: " + reader.readLine());
                Thread.sleep(CLIENT_PAUSE_AFTER_ITERATION);
            }

        }
    }


    /**
     * PrintWriter, конечно, в данном случае неудобен из-за того, что не пробрасывает исключения,
     * когда серверный сокет закрылся, например. Так что используется checkError().
     */
    private static void withPrintWriter(Socket socket) throws InterruptedException, IOException {

        try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            System.out.println("---Соединение установлено\n");

            for (int i = 0; i < CLIENT_ITERATION; i++) {
                Thread.sleep(CLIENT_PAUSE_BEFORE_ITERATION);

                out.println("test 123 qwerty №" + i);
                System.out.println("---Сообщение отправлено...");
                if (out.checkError()) {
                    System.err.println("---Проблемы с OutputStream");
                }

                try {
                    System.out.println("---Получено: " + reader.readLine());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Thread.sleep(CLIENT_PAUSE_AFTER_ITERATION);
            }

        }
    }
}
