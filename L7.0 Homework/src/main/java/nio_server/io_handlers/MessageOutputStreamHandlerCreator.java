package nio_server.io_handlers;

import java.io.OutputStream;

public interface MessageOutputStreamHandlerCreator {
    MessageOutputStreamHandler create(OutputStream outputStream);
}
