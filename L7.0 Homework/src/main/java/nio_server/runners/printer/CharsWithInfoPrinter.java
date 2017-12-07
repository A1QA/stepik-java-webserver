package nio_server.runners.printer;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CharsWithInfoPrinter implements ProcessInputPrinter {
    @Override
    public void print(BufferedReader br, String type) throws IOException {
        List<Character> list = new ArrayList<>(400);
        System.out.println(type + " > " + "(вывод в посимвольном режиме)");

        int ch = br.read();
        while (ch != -1) {
            list.add((char)ch);

            if ("ERROR".equals(type.toUpperCase())){
                System.err.print((char)ch);
            } else {
                System.out.print((char)ch);
            }

            ch = br.read();
        }
        System.out.println();
        System.out.println("OUTPUT > Количество символов (" + type + "): " + list.size());
        list.forEach(System.out::print);
        System.out.println("OUTPUT > Количество символов \\r (" + type + "): " +
                list.stream()
                        .filter(character -> character == '\r' )
                        .count());
    }
}