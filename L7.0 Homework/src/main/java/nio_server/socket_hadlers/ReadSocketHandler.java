package nio_server.socket_hadlers;

import nio_server.Sleeper;
import nio_server.context.ClientContext;
import nio_server.context.GlobalContext;

import java.net.Socket;


public class ReadSocketHandler extends IOSocketHandler {

    public ReadSocketHandler(Socket socket) {
        super(socket);
    }

    @Override
    public void handle() {

        GlobalContext context = GlobalContext.getInstance();
        ClientContext client = context.getClientContext();

        System.out.println(PREFIX + "Соединение установлено");

        for (int i = 0; i < client.iterations(); i++) {

            Sleeper.sleep(client.pauseBeforeIteration());

            System.out.println(PREFIX + "Чтение: ");
            getInputStreamHandler().handle();
            System.out.println(PREFIX + "Прочитано");

            Sleeper.sleep(client.pauseAfterIteration());
        }

    }
}