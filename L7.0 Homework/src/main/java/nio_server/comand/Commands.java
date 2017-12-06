package nio_server.comand;

import java.util.List;

public class Commands <T extends Command> implements Command {

    private List<T> commands;

    public Commands(List<T> commands) {
        this.commands = commands;
    }

    public void addCommand(T command){
        commands.add(command);
    }

    @Override
    public void exec() {
        commands.forEach(Command::exec);
    }
}
