package nio_server.runners.printer;

import nio_server.logger.Logger;

import java.io.BufferedReader;
import java.io.IOException;


// todo если \n -- "OUTPUT >"
// todo реализацию печатающую синхронно и нет
// todo и куда выводить (и ио_хендлеры так же)

public class CharsPrinter implements ProcessInputPrinter {
    private Logger logger = Logger.getInstance();

    @Override
    public void print(BufferedReader br, String type) throws IOException {
        //System.out.println(type + " > " + "(вывод в посимвольном режиме)");
        logger.log(this, type + " > " + "(вывод в посимвольном режиме)");

        boolean firstOnLine = true;
        int ch = br.read();
        while (ch != -1) {

            if (firstOnLine) {
                logger.logLine(type + " > ");
                firstOnLine = false;
            }

            if ("ERROR".equals(type.toUpperCase())) {
                //System.err.print((char)ch);
                logger.err((char) ch);
            } else {
                //System.out.print((char)ch);
                logger.log((char) ch);
            }

            if (((char) ch) == '\n') {
                firstOnLine = true;
            }

            ch = br.read();
        }
    }
}