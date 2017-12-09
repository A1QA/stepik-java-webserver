package nio_server.context;

public class DefaultServerContext implements ServerContext {

    private static DefaultServerContext instance = new DefaultServerContext();
    private DefaultServerContext() {}

    public static ServerContext getInstance() {
        return DefaultServerContext.instance;
    }


    @Override
    public int port() {
        return 5050;
    }

    @Override
    public int bufferSize() {
        return 12;
    }

    @Override
    public int pauseAfterIteration() {
        return 0;
    }

    @Override
    public int lifeTime() {
        return 15000;
    }

    @Override
    public boolean closeClientsSockets() {
        return true;
    }
}
