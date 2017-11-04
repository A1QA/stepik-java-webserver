package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class EchoSocketHandler implements SocketHandler {

    private String endMessage;
    private Socket socket;

    public EchoSocketHandler(Socket socket) {
        this.socket = socket;
        this.endMessage = "Bye.";
    }

    @Override
    public void run() {

        try (InputStream inputStream = socket.getInputStream();
             OutputStream outputStream = socket.getOutputStream()) {

            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(outputStream)), true);

            System.out.println("In run() - " + Thread.currentThread().getName());
            while (!socket.isClosed()) { // (true) -- работать будет, но с java.net.SocketException: Software caused connection abort: recv failed
                                         // если если endMessage неправильный
                String message = in.readLine();
                if (message == null || message.equals(endMessage)) { // message == null -- если endMessage неправильный
                    break;
                }
                out.println(message);
                //System.out.println(message); //  лог входящих сообщений
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println("Socket not closed");
                e.printStackTrace();
            }
        }

        System.out.println("Ended - " + Thread.currentThread().getName());
    }
}
