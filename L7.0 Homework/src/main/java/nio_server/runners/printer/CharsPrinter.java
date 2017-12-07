package nio_server.runners.printer;

import java.io.BufferedReader;
import java.io.IOException;

public class CharsPrinter implements ProcessInputPrinter {
    @Override
    public void print(BufferedReader br, String type) throws IOException {
        System.out.println(type + " > " + "(вывод в посимвольном режиме)");

        int ch = br.read();
        while (ch != -1) {
            if ("ERROR".equals(type.toUpperCase())){
                System.err.print((char)ch);
            } else {
                System.out.print((char)ch);
            }
            ch = br.read();
        }
    }
}