package nio_server.comand;

import java.util.stream.IntStream;

public class RepeatableCommand implements Command {

    private int count;
    private Command command;

    public RepeatableCommand(int count, Command command) {
        this.count = count;
        this.command = command;
    }

    @Override
    public void exec() {
        IntStream.range(0, count).forEach(i -> command.exec());
    }
}
