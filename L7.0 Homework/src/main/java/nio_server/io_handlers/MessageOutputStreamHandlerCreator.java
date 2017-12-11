package nio_server.io_handlers;

import java.io.OutputStream;

@FunctionalInterface
public interface MessageOutputStreamHandlerCreator {
    MessageOutputStreamHandler create(OutputStream outputStream);
}
