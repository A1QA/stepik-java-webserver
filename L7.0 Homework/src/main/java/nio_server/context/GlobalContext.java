package nio_server.context;

import nio_server.logger.Logger;

import static java.util.Objects.isNull;

// todo улучшить синглтоны
public class GlobalContext {

    private static GlobalContext instance = new GlobalContext();
    private ServerContext serverContext;
    private ClientContext clientContext;
    private volatile ProcessContext processContext;
    private Logger logger = Logger.getInstance();

    private GlobalContext() {}


    public static GlobalContext getInstance() {
        return instance;
    }

    public ServerContext getServerContext() {
        if (isNull(this.serverContext)) {
            this.serverContext = DefaultServerContext.getInstance();
            //System.out.println("Установлен ServerContext по-умолчанию");
            logger.log(instance, "Установлен ServerContext по-умолчанию");
        }
        return this.serverContext;
    }

    public ClientContext getClientContext() {
        if (isNull(this.clientContext)) {
            this.clientContext = DefaultClientContext.getInstance();
            //System.out.println("Установлен ClientContext по-умолчанию");
            logger.log(instance, "Установлен ClientContext по-умолчанию");
        }
        return this.clientContext;
    }

    // испоьзуется одновременно двумя потоками
    public ProcessContext getProcessContext() {
        if (isNull(this.processContext)) {
            synchronized (this) {
                if (isNull(this.processContext)) {
                    this.processContext = DefaultProcessContext.getInstance();
                    //System.out.println("Установлен ProcessContext по-умолчанию");
                    logger.log(instance, "Установлен ProcessContext по-умолчанию");
                }
            }
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
