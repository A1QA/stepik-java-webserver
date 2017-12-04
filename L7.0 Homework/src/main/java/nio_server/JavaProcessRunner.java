package nio_server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

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
                String line = null;

//                int ch = br.read();
//                while (ch != -1) {
//                    System.err.print((char)ch);
//                    if (ch == '\n') {
//                        break;
//                    }
//                    ch = br.read();
//                }
//                // TODO: НЕ построчно читать
                while ( (line = br.readLine()) != null) {
                    if ("ERROR".equals(type.toUpperCase())) {
                        System.err.println(type + " > " + line);
                    } else {
                        System.out.println(type + " > " + line);
                    }
                }
            } catch (IOException ex){
                ex.printStackTrace();
            }
        }
    }

//    public static void main(String... args) throws IOException, InterruptedException {
//
//
//        Runtime runtime = Runtime.getRuntime();
//        //Process process = runtime.exec(new String[]{"java", "-version"});
//        Process process = runtime.exec("help");
//
////        ProcessBuilder pb = new ProcessBuilder("echo", "Hello JCG\nThis is ProcessBuilder Example"); // не работает
//
////        ProcessBuilder pb = new ProcessBuilder("java", "-version");
////        Process process = pb.start();
//
//
//        JavaProcessRunner processTest = new JavaProcessRunner("");
//
//        Stream errorGobbler = processTest.new Stream(process.getErrorStream(), "ERROR");
//        Stream outputGobbler = processTest.new Stream(process.getInputStream(), "OUTPUT");
//        errorGobbler.start();
//        outputGobbler.start();
//
//        //process.waitFor(2, TimeUnit.SECONDS);
//        process.waitFor();
//        process.destroy();
//    }
}


