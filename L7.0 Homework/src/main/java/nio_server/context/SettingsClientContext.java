package nio_server.context;

import nio_server.io_handlers.InputStreamHandlerCreator;
import nio_server.io_handlers.MessageOutputStreamHandlerCreator;

import static nio_server.Settings.CLIENT_ITERATION;
import static nio_server.Settings.CLIENT_MESSAGE;
import static nio_server.Settings.CLIENT_PAUSE_AFTER_ITERATION;
import static nio_server.Settings.CLIENT_PAUSE_BEFORE_ITERATION;
import static nio_server.Settings.CLIENT_PAUSE_MIDDLE;
import static nio_server.Settings.INPUT_HANDLER_CREATOR;
import static nio_server.Settings.MESSAGE_OUTPUT_HANDLER_CREATOR;

public class SettingsClientContext implements ClientContext{

    private static SettingsClientContext instance = new SettingsClientContext();
    private SettingsClientContext() {}
    public static SettingsClientContext getInstance() {
        return SettingsClientContext.instance;
    }

    @Override
    public int pauseBeforeIteration() {
        return CLIENT_PAUSE_BEFORE_ITERATION;
    }

    @Override
    public int pauseInMiddleOfIteration() {
        return CLIENT_PAUSE_MIDDLE;
    }

    @Override
    public int pauseAfterIteration() {
        return CLIENT_PAUSE_AFTER_ITERATION;
    }

    @Override
    public int iterations() {
        return CLIENT_ITERATION;
    }

    @Override
    public String message() {
        return CLIENT_MESSAGE;
    }

    @Override
    public MessageOutputStreamHandlerCreator messageWriterCreator() {
        return MESSAGE_OUTPUT_HANDLER_CREATOR;
    }

    @Override
    public InputStreamHandlerCreator inputStreamCreator() {
        return INPUT_HANDLER_CREATOR;
    }
}
