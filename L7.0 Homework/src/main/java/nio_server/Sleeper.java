package nio_server;

public class Sleeper {

    private static final String PREFIX = "Sleeper: ";

    public static void sleep(int millis) {
        Sleeper.sleep(millis, Sleeper.PREFIX);
    }

    public static void sleep(int millis, String prefix){
        if (millis > 0) { // а то интерапт эксепшены даже при 0 могут быть
            try {
                Thread.sleep(millis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println(prefix + "Поток прерыван во время sleep(" + millis + ")");
//              e.printStackTrace();
            }
        }
    }
}

