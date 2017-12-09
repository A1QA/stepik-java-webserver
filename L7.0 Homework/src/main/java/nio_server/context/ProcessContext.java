package nio_server.context;

import nio_server.runners.printer.ProcessInputPrinter;

public interface ProcessContext {
    ProcessInputPrinter printer();
}
