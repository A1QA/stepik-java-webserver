package nio_server.runners;

import nio_server.context.GlobalContext;
import nio_server.context.SettingsClientContext;
import nio_server.socket_hadlers.IOSocketHandler;
import nio_server.socket_hadlers.WriteReadSocketHandler;

import java.io.IOException;
import java.net.Socket;


public class ClientStarter {

    public static void main(String... args) {
        try (Socket socket = new Socket("localhost", 5050)) {

            final GlobalContext context = GlobalContext.getInstance();
            context.setClientContext(SettingsClientContext.getInstance());

            // какой обработчик сокета
            IOSocketHandler socketHandler = new WriteReadSocketHandler(socket);

            // как клиент пишет
            socketHandler.setMessageOutputStreamHandlerCreator(context.getClientContext().messageWriterCreator()); // MessageCharsWriter::new
            // как клиент читает
            socketHandler.setInputStreamHandlerCreator(context.getClientContext().inputStreamCreator()); // InputLineCharsPrinter::new

            socketHandler.init();
            socketHandler.handle();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


