package server;

import java.net.Socket;

public interface SocketHandlerCreator {
    SocketHandler createSocketHandler(Socket socket);
}
