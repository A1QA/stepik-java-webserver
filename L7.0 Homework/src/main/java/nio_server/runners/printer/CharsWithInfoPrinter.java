package nio_server.runners.printer;

import nio_server.logger.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CharsWithInfoPrinter implements ProcessInputPrinter {
    private Logger logger = Logger.getInstance();

    @Override
    public void print(BufferedReader br, String type) throws IOException {
        List<Character> list = new ArrayList<>(400);
        //System.out.println(type + " > " + "(вывод в посимвольном режиме)");
        logger.log(this, type + " > " + "(вывод в посимвольном режиме)");

        boolean firstOnLine = true;
        int ch = br.read();
        while (ch != -1) {
            list.add((char)ch);

            if (firstOnLine) {
                logger.logLine(type + " > ");
                firstOnLine = false;
            }

            if ("ERROR".equals(type.toUpperCase())){
                //System.err.print((char)ch);
                logger.err((char)ch);
            } else {
                //System.out.print((char)ch);
                logger.log((char)ch);
            }

            if (((char) ch) == '\n') {
                firstOnLine = true;
            }

            ch = br.read();
        }
        //System.out.println();
        logger.log("");
        //System.out.println("OUTPUT > Количество символов (" + type + "): " + list.size());
        logger.log("OUTPUT > Количество символов (" + type + "): " + list.size());
        //list.forEach(System.out::print);
        list.forEach(logger::log);
        long rCharsCount = list.stream()
                .filter(character -> character == '\r' )
                .count();
        //System.out.println("OUTPUT > Количество символов \\r (" + type + "): " + rCharsCount);
        logger.log("OUTPUT > Количество символов \\r (" + type + "): " + rCharsCount);
    }
}