package nio_server.runners.printer;

import nio_server.logger.Logger;

import java.io.BufferedReader;
import java.io.IOException;

public class LinePrinter implements ProcessInputPrinter {

    private Logger logger = Logger.getInstance();


    @Override
    public void print(BufferedReader br, String type) throws IOException {
        String line;
        //System.out.println(type + " > " + "(вывод в построчном режиме)");
        logger.log(type + " > " + "(вывод в построчном режиме)");

        while ( (line = br.readLine()) != null) {
            if ("ERROR".equals(type.toUpperCase())) {
                //System.err.println(type + " > " + line);
                logger.err(type + " > " + line);
            } else {
                //System.out.println(type + " > " + line);
                logger.log(type + " > " + line);
            }
        }
    }
}
