package nio_server;

public class Sleeper {
    public static void sleep(int millis) {
        if (millis > 0) { // а то интерапт эксепшены даже при 0 могут быть
            try {
                Thread.sleep(millis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Поток прерыван во время sleep(" + millis + ")");
//              e.printStackTrace();
            }
        }
    }
}
