package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread {
    private static final int PORT = 5050;
    private int workCount; // сколько обработать соединений и завершиться
    private ServerSocket serverSocket;

    public Server() throws IOException {
        serverSocket = new ServerSocket(PORT);
        workCount = 10;
        //setDaemon(true); // иначе java.util.concurrent.ExecutionException: base.d: java.net.SocketException: Connection reset
        System.out.println("Server started");
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
                System.out.println("Wait connection...");
                Socket socket = serverSocket.accept();
                new Thread(new EchoSocketHandler(socket)).start();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        closeSocket();
        System.out.println("Server finished...");
    }

    private void closeSocket(){
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
