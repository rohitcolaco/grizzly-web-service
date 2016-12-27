package main.java.com.rcolaco.boilerplate.monitor;

import javax.management.MBeanInfo;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public final class MBeanUtil
{
    private static final String OBJECT_NAME_PREFIX = "com.rcolaco.boilerplate:type=";
    private static final Logger log = Logger.getLogger(MBeanUtil.class.getName());

    /**
     * @param mbeanName
     * @param instance
     * @throws MalformedObjectNameException
     */
    public static void register(String mbeanName, Object instance) throws MalformedObjectNameException
    {
        final MBeanServer server = ManagementFactory.getPlatformMBeanServer();
        final ObjectName oName = new ObjectName(OBJECT_NAME_PREFIX + mbeanName);
        MBeanInfo info = null;
        try {
            info = server.getMBeanInfo(oName);
        } catch (Exception e) {
            log.log(Level.WARNING, "Couldn't find mbean " + mbeanName + " will try registering it");
        }

        if (info == null) {
            // Register the mbean
            try {
                server.registerMBean(instance, oName);
            } catch (Exception e) {
                log.log(Level.SEVERE, "Error registering mbean " + mbeanName, e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T getAttribute(String mbeanName, String attributeName, Class<T> result) throws MalformedObjectNameException {
        final MBeanServer server = ManagementFactory.getPlatformMBeanServer();
        final ObjectName oName = new ObjectName(OBJECT_NAME_PREFIX + mbeanName);
        try {
            return (T) server.getAttribute(oName, attributeName);
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error getting  attribute " + attributeName + " for mbean " + mbeanName, e);
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T> T invokeMethod(String mbeanName, String methodName, Object[] params, String[] signature) throws MalformedObjectNameException  {
        final MBeanServer server = ManagementFactory.getPlatformMBeanServer();
        final ObjectName oName = new ObjectName(OBJECT_NAME_PREFIX + mbeanName);
        try {
            return (T) server.invoke(oName, methodName, params, signature);
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error invoking method " + methodName + " for mbean " + mbeanName, e);
        }

        return null;
    }


}
