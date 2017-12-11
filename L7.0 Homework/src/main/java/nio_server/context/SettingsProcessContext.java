package nio_server.context;

import nio_server.runners.printer.ProcessInputPrinter;

import java.nio.charset.Charset;

import static nio_server.Settings.PROCESS_INPUT_PRINTER;
import static nio_server.Settings.PROCESS_OUTPUT_CHARSET;

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

    @Override
    public Charset charset() {
        return PROCESS_OUTPUT_CHARSET;
    }
}
