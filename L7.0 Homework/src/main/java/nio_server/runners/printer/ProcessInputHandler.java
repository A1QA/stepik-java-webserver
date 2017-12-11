package nio_server.runners.printer;

import nio_server.context.GlobalContext;
import nio_server.logger.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Objects;

public class ProcessInputHandler extends Thread {
    private InputStream is;
    private String type;
    private Charset charset = GlobalContext.getInstance().getProcessContext().charset();
    private ProcessInputPrinter printer;
    private Logger logger = Logger.getInstance();

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    public void setPrinter(ProcessInputPrinter printer) {
        this.printer = printer;
    }

    public ProcessInputHandler(InputStream is, String type) {
        this.is = is;
        this.type = type;
    }

    @Override
    public void run() {
        try {
            InputStreamReader isr = new InputStreamReader(is, charset);
            BufferedReader br = new BufferedReader(isr);

            if (!Objects.isNull(printer)) {
                printer.print(br, type);
            } else {
                //System.err.println("ProcessInputPrinter не установлен.");
                logger.err(this, "ProcessInputPrinter не установлен.");
            }

        } catch (IOException ex){
            ex.printStackTrace();
        }
    }
}