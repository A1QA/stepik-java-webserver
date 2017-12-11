package nio_server.io_handlers;

import nio_server.logger.Logger;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;

import static java.util.Objects.isNull;



/**
 * PrintWriter, конечно, в данном случае неудобен из-за того, что не пробрасывает исключения,
 * когда серверный сокет закрылся, например. Так что используется checkError().
 */
public class MessagePrintWriter extends MessageOutputStreamHandler {

    private PrintWriter writer;
    private Logger logger = Logger.getInstance();

    public MessagePrintWriter(OutputStream output) {
        this.writer = new PrintWriter(new OutputStreamWriter(output));
    }
    public MessagePrintWriter(Writer writer) {
        this.writer = new PrintWriter(writer);
    }
    public MessagePrintWriter(PrintWriter writer) {
        this.writer = writer;
    }

    @Override
    public void handle() {
        if (!isNull(getMessage())) {
            writer.println(getMessage());

            if (writer.checkError()) {
                //System.err.println("Проблемы с MessagePrintWriter");
                logger.log(this,"Проблемы с MessagePrintWriter");
            }
        }
    }
}