package nio_server.socket_hadlers;

import nio_server.Sleeper;

import java.net.Socket;

import static nio_server.Settings.CLIENT_ITERATION;
import static nio_server.Settings.CLIENT_PAUSE_AFTER_ITERATION;
import static nio_server.Settings.CLIENT_PAUSE_MIDDLE;

public class ReadHandler extends IOHandler {

    public ReadHandler(Socket socket) {
        super(socket);
    }

    @Override
    public void handle() {

        System.out.println(PREFIX + "Соединение установлено");

        for (int i = 0; i < CLIENT_ITERATION; i++) {

            Sleeper.sleep(CLIENT_PAUSE_MIDDLE);

            System.out.println(PREFIX + "Чтение: ");
            getInputStreamHandler().handle();
            System.out.println(PREFIX + "Прочитано");

            Sleeper.sleep(CLIENT_PAUSE_AFTER_ITERATION);
        }

    }
}