package nio_server.runners;

import nio_server.context.DefaultClientContext;
import nio_server.context.GlobalContext;
import nio_server.socket_hadlers.IOHandler;
import nio_server.socket_hadlers.WriteReadHandler;

import java.io.IOException;
import java.net.Socket;


public class ClientStarter {

    public static void main(String... args) {
        try (Socket socket = new Socket("localhost", 5050)) {

            final GlobalContext context = GlobalContext.getInstance();
            context.setClientContext(DefaultClientContext.getInstance());

            // какой обработчик
            IOHandler ioHandler = new WriteReadHandler(socket);

            // как клиент пишет
            ioHandler.setMessageOutputStreamHandlerCreator(context.getClientContext().messageWriterCreator()); // MessageCharsWriter::new
            // как клиент читает
            ioHandler.setInputStreamHandlerCreator(context.getClientContext().inputStreamCreator()); // InputLineCharsPrinter::new

            ioHandler.init();
            ioHandler.handle();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


