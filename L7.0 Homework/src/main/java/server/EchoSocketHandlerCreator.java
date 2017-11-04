package server;

import java.net.Socket;

public class EchoSocketHandlerCreator implements SocketHandlerCreator {
    @Override
    public SocketHandler createSocketHandler(Socket socket) {
        return new EchoSocketHandler(socket);
    }
}
