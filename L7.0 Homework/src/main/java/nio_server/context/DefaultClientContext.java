package nio_server.context;

import nio_server.io_handlers.InputLinePrinter;
import nio_server.io_handlers.InputStreamHandlerCreator;
import nio_server.io_handlers.MessageBufferedWriter;
import nio_server.io_handlers.MessageOutputStreamHandlerCreator;

public class DefaultClientContext implements ClientContext {

    private static DefaultClientContext instance = new DefaultClientContext();

    private DefaultClientContext() {}

    public static DefaultClientContext getInstance() {
        return DefaultClientContext.instance;
    }


    @Override
    public int pauseBeforeIteration() {
        return 0;
    }

    @Override
    public int pauseInMiddleOfIteration() {
        return 0;
    }

    @Override
    public int pauseAfterIteration() {
        return 0;
    }

    @Override
    public int iterations() {
        return 5;
    }

    @Override
    public String message() {
        return "QWERTY1234TEST";
    }

    @Override
    public MessageOutputStreamHandlerCreator messageWriterCreator() {
        return MessageBufferedWriter::new;
    }

    @Override
    public InputStreamHandlerCreator inputStreamCreator() {
        return InputLinePrinter::new;
    }
}
