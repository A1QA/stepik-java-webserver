package nio_server.context;

import static java.util.Objects.isNull;


// todo улучшить синглтоны

public class GlobalContext {

    private static GlobalContext instance = new GlobalContext();
    private ServerContext serverContext;
    private ClientContext clientContext;
    private ProcessContext processContext;

    private GlobalContext() {}


    public static GlobalContext getInstance() {
        return instance;
    }

    public ServerContext getServerContext() {
        if (isNull(this.serverContext)) {
            throw new RuntimeException("ServerContext is null");
        }
        return this.serverContext;
    }

    public ClientContext getClientContext() {
        if (isNull(this.clientContext)) {
            throw new RuntimeException("ClientContext is null");
        }
        return this.clientContext;
    }

    public ProcessContext getProcessContext() {
        if (isNull(this.processContext)) {
            throw new RuntimeException("ProcessContext is null");
        }
        return this.processContext;
    }


    public void setServerContext(ServerContext serverContext) {
        this.serverContext = serverContext;
    }

    public void setClientContext(ClientContext clientContext) {
        this.clientContext = clientContext;
    }

    public void setProcessContext(ProcessContext processContext) {
        this.processContext = processContext;
    }

}
