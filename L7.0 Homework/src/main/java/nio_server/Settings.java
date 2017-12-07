package nio_server;


    /*
    999 2299 0 999
    Сервер прочел половину.

    999 999 1999 999
    сразу же закрыл
    */


// TODO: this
public final class Settings {

    // Runner
    public static final boolean RUN_CLIENT_IN_NEW_PROCESS = true;

    // ServerStarter
    public static final int SERVER_LIFE_TIME   = 5299;
    public static final int SERVER_PORT = 5050;

    // Server
    public static final int BUFFER_SIZE = 12;
    public static final int SERVER_PAUSE_AFTER = 999;
    public static final boolean SERVER_CLOSES_CLIENT_SOCKETS = true;

    // IOHandler: WriteReadHandler, ReadHandler
    public static final int CLIENT_PAUSE_BEFORE_ITERATION = 0;
    public static final int CLIENT_PAUSE_AFTER_ITERATION  = 999;
    public static final int CLIENT_PAUSE_MIDDLE  = 350;
    public static final int CLIENT_ITERATION = 3;

    // WriteReadHandler
    public static final String CLIENT_MESSAGE = "TEST1234567890QWERTY№";


    // JavaProcessRunner -<- ProcessInputPrinter
    // ClientStarter -<- IOHandler -<- InputStreamHandlerCreator, MessageOutputStreamHandlerCreator

}




//class DynamicSettings {
//
//    private int serverPauseAfter;
//    private int serverLifeTime;
//    private int clientPause1;
//    private int clientPause2;
//
//    private boolean clientUsePrintWriter;
//    private boolean serverClosesClientsSockets;
//    private boolean runClientInNewProcess;
//
//    private int bufferSize;
//    private int serverPort;
//
//}
