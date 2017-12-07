package nio_server.comand;

import nio_server.io_handlers.InputStreamHandler;

public class ReadCommand implements Command {

    private InputStreamHandler handler;

    public ReadCommand(InputStreamHandler handler) {
        this.handler = handler;
    }

    @Override
    public void exec() {
        handler.handle();
    }
}
