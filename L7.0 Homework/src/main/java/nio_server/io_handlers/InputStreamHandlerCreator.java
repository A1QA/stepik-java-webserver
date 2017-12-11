package nio_server.io_handlers;

import java.io.InputStream;

@FunctionalInterface
public interface InputStreamHandlerCreator {
    InputStreamHandler create(InputStream inputStream);
}
