package nio_server.io_handlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class InputLinePrinter implements InputStreamHandler {

    private BufferedReader reader;

    public InputLinePrinter(InputStream input) {
        reader = new BufferedReader(new InputStreamReader(input));
    }
    public InputLinePrinter(Reader reader) {
        this.reader = new BufferedReader(reader);
    }
    public InputLinePrinter(BufferedReader reader) {
        this.reader = reader;
    }

    @Override
    public void handle() {
        try {
            System.out.println(reader.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
