package nio_server;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.Socket;

import static nio_server.Server.CLIENT_PAUSE1;
import static nio_server.Server.CLIENT_PRINT_WRITER;
import static nio_server.Server.CLIENT_PAUSE2;

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



    // todo: читать в цикле
    private static void withWriter(Socket socket) throws InterruptedException, IOException {

        try (Writer out = new OutputStreamWriter(socket.getOutputStream());
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            System.out.println("---Соединение установлено\n");
            System.out.println(socket.getReceiveBufferSize());
            System.out.println(socket.getSendBufferSize());

            for (int i = 0; i < 8; i++) {

/*                try {
                    System.out.println("---Чтение: " + reader.readLine());
                } catch (IOException e) {
                    e.printStackTrace();
                }*/

                System.out.println("---Подготовка к отправке... "+i);
                Thread.sleep(CLIENT_PAUSE1);
                try {
                    //out.write("test 123 qwerty №" + i);
                    //out.write("test 123 qwerty №\n" + i); // reader.readLine() ожидает \n
                    out.write(new char[6553]);
                    out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.print("---Сообщение отправлено... "+i+"\n");

                Thread.sleep(350);
/*                try {
                    System.out.println("---Чтение: ");
                    System.out.println("---" + reader.readLine());
                    System.out.println("---Прочитано");
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
                Thread.sleep(CLIENT_PAUSE2);
            }

        }
    }


    private static void withBufferedWriter(Socket socket) throws InterruptedException, IOException {

        try (BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            System.out.println("---Соединение установлено\n");

            for (int i = 0; i < 5; i++) {

                System.out.println("---Подготовка к отправке...");
                //out.write("test 123 qwerty №" + i);
                out.write("test 123 qwerty №\n" + i); // reader.readLine() ожидает \n
                out.flush();
                System.out.println("---Сообщение отправлено...");

                //Thread.sleep(333);
                System.out.println("---Получено: " + reader.readLine());
                Thread.sleep(CLIENT_PAUSE2);
            }

        }
    }

    private static void withPrintWriter(Socket socket) throws InterruptedException, IOException {

        try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            System.out.println("---Соединение установлено\n");

            for (int i = 0; i < 5; i++) {
                Thread.sleep(CLIENT_PAUSE1);
                out.println("test 123 qwerty №" + i);
                System.out.println("---Сообщение отправлено...");
                // PrintWriter, конечно, в данном случае неудобен из-за того, что не пробрасывает исключения?
                // когда серверный сокет закрылся, например.
                if (out.checkError()) {
                    System.err.println("---Проблемы с OutputStream");
                }

                //Thread.sleep(333);
                //System.out.println("---Получено: " + reader.readLine());
                try {
                    System.out.println("---Получено: " + reader.readLine());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Thread.sleep(CLIENT_PAUSE2);
            }

        }
    }
}
