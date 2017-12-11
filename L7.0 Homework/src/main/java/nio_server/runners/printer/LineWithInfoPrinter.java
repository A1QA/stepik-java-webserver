package nio_server.runners.printer;

import nio_server.logger.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LineWithInfoPrinter implements ProcessInputPrinter {

    private Logger logger = Logger.getInstance();


    @Override
    public void print(BufferedReader br, String type) throws IOException {
        List<Character> list = new ArrayList<>(400);
        String line;
        //System.out.println(type + " > " + "(вывод в построчном режиме)");
        logger.log(type + " > " + "(вывод в построчном режиме)");

        while ( (line = br.readLine()) != null) {
            List<Character> collect = line.chars()
                    .mapToObj(value -> ((char) value))
                    .collect(Collectors.toList()); // todo сделать коллектор для IntStream
            list.addAll(collect);
            list.add('\n');
            if ("ERROR".equals(type.toUpperCase())) {
                //System.err.println(type + " > " + line);
                logger.err(type + " > " + line);
            } else {
                //System.out.println(type + " > " + line);
                logger.log(type + " > " + line);
            }
        }
        //System.out.println();
        logger.log("");
        //System.out.println("OUTPUT > Количество символов (" + type + ") без учета \\r, \\n: " + list.size());
        logger.log("OUTPUT > Количество символов (" + type + ") без учета \\r, \\n: " + list.size());
        //list.forEach(System.out::print);
        list.forEach(logger::log);
    }
}