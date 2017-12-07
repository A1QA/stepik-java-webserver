package nio_server.comand;

import nio_server.socket_hadlers.SocketHandler;

public class SocketHandlerCommand implements Command  {

    private SocketHandler socketHandler;

    public SocketHandlerCommand(SocketHandler socketHandler) {
        this.socketHandler = socketHandler;
    }

    @Override
    public void exec() {
        socketHandler.handle();
    }
}
