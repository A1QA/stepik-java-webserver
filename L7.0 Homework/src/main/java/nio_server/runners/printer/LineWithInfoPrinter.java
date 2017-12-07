package nio_server.runners.printer;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LineWithInfoPrinter implements ProcessInputPrinter {
    @Override
    public void print(BufferedReader br, String type) throws IOException {
        List<Character> list = new ArrayList<>(400);
        String line;
        System.out.println(type + " > " + "(вывод в построчном режиме)");

        while ( (line = br.readLine()) != null) {
            List<Character> collect = line.chars()
                    .mapToObj(value -> ((char) value))
                    .collect(Collectors.toList()); // todo сделать коллектор для IntStream
            list.addAll(collect);
            list.add('\n');
            if ("ERROR".equals(type.toUpperCase())) {
                System.err.println(type + " > " + line);
            } else {
                System.out.println(type + " > " + line);
            }
        }
        System.out.println();
        System.out.println("OUTPUT > Количество символов (" + type + ") без учета \\r, \\n: " + list.size());
        list.forEach(System.out::print);
    }
}