package nio_server.runners;

import nio_server.io_handlers.InputLineCharsPrinter;
import nio_server.io_handlers.MessageBufferedWriter;
import nio_server.io_handlers.MessageCharsWriter;
import nio_server.socket_hadlers.IOHandler;
import nio_server.socket_hadlers.ReadHandler;
import nio_server.socket_hadlers.WriteReadHandler;

import java.io.IOException;
import java.net.Socket;


public class ClientStarter {

    public static void main(String... args) {
        try (Socket socket = new Socket("localhost", 5050)) {

            // какой обработчик
            IOHandler ioHandler = new WriteReadHandler(socket);

            // как клиент пишет
            ioHandler.setMessageOutputStreamHandlerCreator(MessageCharsWriter::new);
            // как клиен читает
            ioHandler.setInputStreamHandlerCreator(InputLineCharsPrinter::new);

            ioHandler.init();
            ioHandler.handle();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


