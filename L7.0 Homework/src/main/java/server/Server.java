package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread {
    private static final int PORT = 5050;
    private int workCount;
    private ServerSocket serverSocket;

    public Server() throws IOException {
        serverSocket = new ServerSocket(PORT);
        workCount = 10;
        start();
    }

    @Override
    public void run() {

        while (true) {
            System.out.println(workCount);
            try {
                if (workCount == 0) {
                    break;
                }
                --workCount;
                System.out.println("Wait...");
                Socket socket = serverSocket.accept();
                new Thread(new EchoSocketHandler(socket)).start();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Server finished...");
    }


}
