package nio_server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

/**
 * https://javatalks.ru/topics/10697
 */

public class JavaProcessRunner {

    //public static final String CLIENT2 = "java -classpath ./target/classes main.Main";
    //public static final String CLIENT3 = "java -classpath \"./L7.0 Homework/target/classes\" main.Main";

    public static final String CLASS_PATH = "\"./L7.0 Homework/target/classes\"";
    public static final String CLASS = "nio_server.Client";
    public static String getProcessCommand(String classPath, String clazz) {
        return "java -classpath " + classPath + " " + clazz;
    }

    private Process process;
    private String command;

    public void waitFor() throws InterruptedException {
        process.waitFor();
    }
    public void waitFor(long timeout, TimeUnit unit) throws InterruptedException {
        process.waitFor(timeout, unit);
    }


    private Charset charset = Charset.forName("CP1251"); // utf-8, CP866, CP1251

    public void setCharset(Charset charset) {
        this.charset = charset;
    }


    public JavaProcessRunner(String command) {
        this.command = command;
    }

    public void runProcess() throws IOException {
        Runtime runtime = Runtime.getRuntime();
        this.process = runtime.exec(command); // throws IOException

        Stream errorGobbler = new Stream(this.process.getErrorStream(), "ERROR");
        Stream outputGobbler = new Stream(this.process.getInputStream(), "OUTPUT");
        errorGobbler.start();
        outputGobbler.start();
    }

    public void destroy() {
        if (!isNull(this.process)) {
            this.process.destroy();
        }
    }


    private class Stream extends Thread {
        private InputStream is;
        private String type;

        Stream(InputStream is, String type) {
            this.is = is;
            this.type = type;
        }


        public void run() {
            try {
                InputStreamReader isr = new InputStreamReader(is, JavaProcessRunner.this.charset);
                BufferedReader br = new BufferedReader(isr);

                consoleOut4(br);

            } catch (IOException ex){
                ex.printStackTrace();
            }
        }

        private void consoleOut1(BufferedReader br) throws IOException {
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

        private void consoleOut2(BufferedReader br) throws IOException {
            String line;

            while ( (line = br.readLine()) != null) {
                List<Character> collect = line.chars().mapToObj(value -> ((char) value))
                        .collect(Collectors.toList());
                if ("ERROR".equals(type.toUpperCase())) {
                    System.err.println(type + " > " + line);
                } else {
                    System.out.println(type + " > " + line);
                }
            }
        }

        private void consoleOut3(BufferedReader br) throws IOException {

            List<Character> list = new ArrayList<>(400);

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

        private void consoleOut4(BufferedReader br) throws IOException {
            List<Character> list = new ArrayList<>(400);
            String line;

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
}


