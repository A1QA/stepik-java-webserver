package nio_server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import static java.util.Objects.isNull;

// https://javatalks.ru/topics/10697

public class ProcessTest {

    //public static final String CLIENT2 = "java -classpath ./target/classes main.Main";
    //public static final String CLIENT3 = "java -classpath \"./L7.0 Homework/target/classes\" main.Main";
    public static final String START_CLIENT = "java -classpath \"./L7.0 Homework/target/classes\" nio_server.Client";


    private Process process;
    private String command;

    public ProcessTest(String command) {
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
}


class Stream extends Thread {
    private InputStream is;
    private String type;

    Stream(InputStream is, String type) {
        this.is = is;
        this.type = type;
    }

    public void run() {
        try {
            InputStreamReader isr = new InputStreamReader(is, Charset.forName("CP1251"));
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while ( (line = br.readLine()) != null) {
                System.out.println(type + " > " + line);
            }
        } catch (IOException ex){
            ex.printStackTrace();
        }
    }
}
