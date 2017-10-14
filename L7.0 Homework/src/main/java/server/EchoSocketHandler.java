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

public class EchoSocketHandler implements Runnable {

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
            while (!socket.isClosed()) {
                String message = in.readLine();
                if (message.equals(endMessage)) { // message == null -- можно и так сделать
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
