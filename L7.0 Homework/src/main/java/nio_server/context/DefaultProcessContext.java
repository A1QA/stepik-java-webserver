package nio_server.context;

import nio_server.runners.printer.LinePrinter;
import nio_server.runners.printer.ProcessInputPrinter;

public class DefaultProcessContext implements ProcessContext {

    private static DefaultProcessContext instance = new DefaultProcessContext();
    private DefaultProcessContext() {}

    public static ProcessContext getInstance() {
        return DefaultProcessContext.instance;
    }

    @Override
    public ProcessInputPrinter printer() {
        return new LinePrinter();
    }
}
