package nio_server.comand;

import nio_server.io_handlers.InputLineCharsPrinter;
import nio_server.io_handlers.InputStreamHandler;
import nio_server.io_handlers.MessageBufferedWriter;
import nio_server.io_handlers.MessageCharsWriter;
import nio_server.io_handlers.MessageOutputStreamHandler;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.function.Supplier;

public class Main {


    public static void main(String... args) throws IOException {

        final String PREFIX = "ClientStarter: ";

//        Commands<Command> commands = new Commands<>(new ArrayList<>());
//        Command command1 = () -> System.out.println(1);
//        Command command2 = () -> System.out.println(2);
//        commands.addCommand(command1);
//        commands.addCommand(command2);
//        commands.exec();

        try (Socket socket = new Socket("localhost", 5050)) {
            InputStreamHandler inputStreamHandler = new InputLineCharsPrinter(socket.getInputStream());
            MessageOutputStreamHandler messageOutputStreamHandler = new MessageBufferedWriter(socket.getOutputStream());

            Commands<Command> commands = new Commands<>(new ArrayList<>());
            commands.addCommand(new PrintCommand(PREFIX + "Соединение установлено"));
            commands.addCommand(System.out::println);
            commands.addCommand(new WriteMessageCommand(new MessageWithCount(PREFIX), new MessageCharsWriter(socket.getOutputStream())));
            commands.addCommand(new PrintCommand(PREFIX + "Соединение установлено"));
        }

    }

    static class CallCounter implements Supplier<Integer> {
        private int i = 0;

        @Override
        public Integer get() {
            return i++;
        }
    }

    static class MessageWithCount implements Supplier<String> {
        private int i = 0;
        private String massage;

        MessageWithCount(String massage) {
            this.massage = massage;
        }

        @Override
        public String get() {
            i++;
            return massage + i;
        }
    }
}

