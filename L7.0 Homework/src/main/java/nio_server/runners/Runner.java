package nio_server.runners;

import static nio_server.Settings.RUN_CLIENT_IN_NEW_PROCESS;


public class Runner {
    public static void main(String... args) {
        if (RUN_CLIENT_IN_NEW_PROCESS) {
            ProcessRunner.main();
        } else {
            ThreadRunner.main();
        }
    }
}
