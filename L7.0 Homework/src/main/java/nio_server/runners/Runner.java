package nio_server.runners;

import static nio_server.Settings.RUN_CLIENT_IN_NEW_PROCESS;

public class Runner { // todo логгер свой написать
    public static void main(String... args) {
        if (RUN_CLIENT_IN_NEW_PROCESS) {
            ProcessRunner.main();
        } else {
            ThreadRunner.main();
        }
    }
}
