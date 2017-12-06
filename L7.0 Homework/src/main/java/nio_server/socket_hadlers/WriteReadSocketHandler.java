package nio_server.socket_hadlers;

import nio_server.Sleeper;

import java.net.Socket;

import static nio_server.Settings.CLIENT_ITERATION;
import static nio_server.Settings.CLIENT_MESSAGE;
import static nio_server.Settings.CLIENT_PAUSE_AFTER_ITERATION;
import static nio_server.Settings.CLIENT_PAUSE_BEFORE_ITERATION;
import static nio_server.Settings.CLIENT_PAUSE_MIDDLE;

public class WriteReadSocketHandler extends IOSocketHandler {

    private static final String PREFIX = "Client: ";

    public WriteReadSocketHandler(Socket socket) {
        super(socket);
    }

    @Override
    public void handle() {

            System.out.println(PREFIX + "Соединение установлено");
            System.out.println();

            for (int i = 0; i < CLIENT_ITERATION; i++) {

                System.out.println(PREFIX + "Подготовка к отправке... "+i);
                Sleeper.sleep(CLIENT_PAUSE_BEFORE_ITERATION);

                getMessageOutputStreamHandler().setMessage(CLIENT_MESSAGE + i);
                getMessageOutputStreamHandler().handle();

                System.out.println(PREFIX + "Сообщение отправлено... "+i);


                Sleeper.sleep(CLIENT_PAUSE_MIDDLE);


                System.out.println(PREFIX + "Чтение: ");
                getInputStreamHandler().handle();
                System.out.println(PREFIX + "Прочитано");

                Sleeper.sleep(CLIENT_PAUSE_AFTER_ITERATION);
            }

    }
}
