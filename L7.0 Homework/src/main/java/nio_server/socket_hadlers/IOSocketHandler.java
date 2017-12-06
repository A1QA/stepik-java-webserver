package nio_server.socket_hadlers;


import nio_server.io_handlers.InputStreamHandler;
import nio_server.io_handlers.MessageOutputStreamHandler;

import java.net.Socket;

public abstract class IOSocketHandler implements SocketHandler {

    protected Socket socket;
    private InputStreamHandler inputStreamHandler;
    private MessageOutputStreamHandler messageOutputStreamHandler;


    public IOSocketHandler(Socket socket) {
        this.socket = socket;
    }

    public InputStreamHandler getInputStreamHandler() {
        return inputStreamHandler;
    }

    public void setInputStreamHandler(InputStreamHandler inputStreamHandler) {
        this.inputStreamHandler = inputStreamHandler;
    }

    public MessageOutputStreamHandler getMessageOutputStreamHandler() {
        return messageOutputStreamHandler;
    }

    public void setMessageOutputStreamHandler(MessageOutputStreamHandler messageOutputStreamHandler) {
        this.messageOutputStreamHandler = messageOutputStreamHandler;
    }

}
