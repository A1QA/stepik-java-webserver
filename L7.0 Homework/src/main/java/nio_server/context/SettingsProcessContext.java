package nio_server.context;

import nio_server.runners.printer.ProcessInputPrinter;

import static nio_server.Settings.PROCESS_INPUT_PRINTER;

public class SettingsProcessContext implements ProcessContext {

    private static SettingsProcessContext instance = new SettingsProcessContext();
    private SettingsProcessContext() {}
    public static ProcessContext getInstance() {
        return SettingsProcessContext.instance;
    }

    @Override
    public ProcessInputPrinter printer() {
        return PROCESS_INPUT_PRINTER;
    }
}
