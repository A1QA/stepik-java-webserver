package nio_server.context;

import nio_server.io_handlers.InputStreamHandlerCreator;
import nio_server.io_handlers.MessageOutputStreamHandlerCreator;

public interface ClientContext {

    int pauseBeforeIteration();
    int pauseInMiddleOfIteration();
    int pauseAfterIteration();
    int iterations();

    String message();

    MessageOutputStreamHandlerCreator messageWriterCreator();
    InputStreamHandlerCreator inputStreamCreator();

}
