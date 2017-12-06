package nio_server;

import nio_server.comand.Command;
import nio_server.comand.Commands;
import nio_server.io_handlers.InputLineCharsPrinter;
import nio_server.io_handlers.InputStreamHandler;
import nio_server.io_handlers.MessageBufferedWriter;
import nio_server.io_handlers.MessageOutputStreamHandler;
import nio_server.socket_hadlers.WriteReadSocketHandler;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;


public class Client {

    //private static final String PREFIX = "Client: ";

    public static void main(String... args) throws IOException {

//        Commands<Command> commands = new Commands<>(new ArrayList<>());
//        Command command1 = () -> System.out.println(1);
//        Command command2 = () -> System.out.println(2);
//        commands.addCommand(command1);
//        commands.addCommand(command2);
//        commands.exec();


        try (Socket socket = new Socket("localhost", 5050)) {

            InputStreamHandler inputStreamHandler = new InputLineCharsPrinter(socket.getInputStream());
            MessageOutputStreamHandler messageOutputStreamHandler = new MessageBufferedWriter(socket.getOutputStream());

            WriteReadSocketHandler socketHandler = new WriteReadSocketHandler(socket);
            socketHandler.setInputStreamHandler(inputStreamHandler);
            socketHandler.setMessageOutputStreamHandler(messageOutputStreamHandler);
            socketHandler.handle();
        }
    }
}

    // todo собирать обработчики из списка _команд_


