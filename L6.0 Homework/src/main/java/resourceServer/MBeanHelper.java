package resourceServer;

import resources.Resource;
import xml.XMLObjHandler;
import xml.Parser;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.logging.Logger;

public class MBeanHelper {
    private static final Logger logger = Logger.getLogger(MBeanHelper.class.getName());

    public static void registerMBean(String path) {
        logger.info("Invoke MBeanHelper.registerMBean() with path: " + path);

        Resource resource = (Resource) Parser.readObject("./" + path, new XMLObjHandler());
        ResourceServerMBean mBean = new ResourceServer(resource);

        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        try {
            ObjectName name = new ObjectName("Admin:type=ResourceServerController"); // MalformedObjectNameException
            mbs.registerMBean(mBean, name); // NotCompliantMBeanException, InstanceAlreadyExistsException, MBeanRegistrationException
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
