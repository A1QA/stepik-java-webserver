package nio_server.context;

public interface ServerContext {
    int port();
    int bufferSize();
    int pauseAfterIteration();
    int lifeTime();
    boolean closeClientsSockets();
}