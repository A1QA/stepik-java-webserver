package nio_server.comand;

import nio_server.io_handlers.MessageOutputStreamHandler;

import java.util.function.Supplier;

public class WriteMessageCommand implements Command {

    private Supplier<String> supplier;
    private MessageOutputStreamHandler handler;

    public WriteMessageCommand(Supplier<String> supplier, MessageOutputStreamHandler handler) {
        this.supplier = supplier;
        this.handler = handler;
    }

    @Override
    public void exec() {
        handler.setMessage(supplier.get());
        handler.handle();
    }

}
