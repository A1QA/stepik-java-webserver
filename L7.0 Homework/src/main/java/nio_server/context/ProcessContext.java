package nio_server.context;

import nio_server.runners.printer.ProcessInputPrinter;

import java.nio.charset.Charset;

public interface ProcessContext {
    ProcessInputPrinter printer();
    Charset charset();
}
