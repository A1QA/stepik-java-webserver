package nio_server.io_handlers;

public abstract class MessageOutputStreamHandler implements OutputStreamHandler {

    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
