package nio_server.io_handlers;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import static java.util.Objects.isNull;

public class MessageBufferedWriter extends MessageOutputStreamHandler {

    private BufferedWriter writer;

    public MessageBufferedWriter(OutputStream output) {
        this.writer = new BufferedWriter(new OutputStreamWriter(output));
    }
    public MessageBufferedWriter(Writer writer) {
        this.writer = new BufferedWriter(writer);
    }
    public MessageBufferedWriter(BufferedWriter writer) {
        this.writer = writer;
    }

    @Override
    public void handle() {
        if (!isNull(getMessage())) {
            try {
                writer.write(getMessage());
                writer.newLine();
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}