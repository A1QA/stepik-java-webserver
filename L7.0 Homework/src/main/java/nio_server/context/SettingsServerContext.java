package nio_server.context;

import static nio_server.Settings.BUFFER_SIZE;
import static nio_server.Settings.SERVER_CLOSES_CLIENT_SOCKETS;
import static nio_server.Settings.SERVER_LIFE_TIME;
import static nio_server.Settings.SERVER_PAUSE_AFTER;
import static nio_server.Settings.SERVER_PORT;

public class SettingsServerContext implements ServerContext {

    private static SettingsServerContext instance = new SettingsServerContext();
    private SettingsServerContext() {}
    public static SettingsServerContext getInstance() {
        return SettingsServerContext.instance;
    }

    @Override
    public int port() {
        return SERVER_PORT;
    }

    @Override
    public int bufferSize() {
        return BUFFER_SIZE;
    }

    @Override
    public int pauseAfterIteration() {
        return SERVER_PAUSE_AFTER;
    }

    @Override
    public int lifeTime() {
        return SERVER_LIFE_TIME;
    }

    @Override
    public boolean closeClientsSockets() {
        return SERVER_CLOSES_CLIENT_SOCKETS;
    }
}
