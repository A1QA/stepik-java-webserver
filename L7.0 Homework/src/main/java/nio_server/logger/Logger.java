package nio_server.logger;


public class Logger {

    private static Logger logger = new Logger();
    private Logger() {}

    public static Logger getInstance() {
        return logger;
    }


    public synchronized void logLine(String s) {
        System.out.print(s);
    }

    public synchronized void log(String s) {
        System.out.println(s);
    }
    public synchronized void log(Object obj, String str){
        log(obj.getClass(), str);
    }
    public synchronized void log(Class<?> clazz, String str){
        System.out.println(clazz.getSimpleName() + ": " + str);
    }

    public synchronized void log(char ch) {
        System.out.print(ch);
    }
    public synchronized void err(char ch){
        System.out.print(ch);
    }

    public synchronized void err(String s) {
        System.err.println(s);
    }
    public synchronized void err(Object clazz, String str){
        System.err.println(clazz.getClass().getSimpleName() + ": " + str);
    }


}
