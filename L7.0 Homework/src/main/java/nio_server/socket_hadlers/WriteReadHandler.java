package nio_server.socket_hadlers;

import nio_server.Sleeper;
import nio_server.io_handlers.InputStreamHandler;

import java.net.Socket;

import static nio_server.Settings.CLIENT_ITERATION;
import static nio_server.Settings.CLIENT_MESSAGE;
import static nio_server.Settings.CLIENT_PAUSE_AFTER_ITERATION;
import static nio_server.Settings.CLIENT_PAUSE_BEFORE_ITERATION;
import static nio_server.Settings.CLIENT_PAUSE_MIDDLE;

public class WriteReadHandler extends IOHandler {

    public WriteReadHandler(Socket socket) {
        super(socket);
    }

    @Override
    public void handle() {
        // todo собирать обработчики из списка _команд_

        System.out.println(PREFIX + "Соединение установлено");

        for (int i = 0; i < CLIENT_ITERATION; i++) {

            System.out.println(PREFIX + "Подготовка к отправке... " + i);
            Sleeper.sleep(CLIENT_PAUSE_BEFORE_ITERATION);

            getMessageOutputStreamHandler().setMessage(CLIENT_MESSAGE + i);
            getMessageOutputStreamHandler().handle();

            System.out.println(PREFIX + "Сообщение отправлено... " + i);


            Sleeper.sleep(CLIENT_PAUSE_MIDDLE);


            System.out.println(PREFIX + "Чтение: ");
            getInputStreamHandler().handle();
            System.out.println(PREFIX + "Прочитано");

            Sleeper.sleep(CLIENT_PAUSE_AFTER_ITERATION);
        }

    }
}
