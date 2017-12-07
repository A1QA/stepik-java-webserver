package nio_server.comand;

public class PrintCommand implements Command {

    private String message;

    public PrintCommand(String message) {
        this.message = message;
    }

    @Override
    public void exec() {
        System.out.println(message);
    }
}
