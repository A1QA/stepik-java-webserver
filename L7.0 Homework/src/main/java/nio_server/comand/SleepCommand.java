package nio_server.comand;

import nio_server.Sleeper;

public class SleepCommand implements Command {

    private int millis;

    public SleepCommand(int millis) {
        this.millis = millis;
    }

    @Override
    public void exec() {
        Sleeper.sleep(millis);
    }
}
