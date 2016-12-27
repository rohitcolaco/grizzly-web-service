package main.java.com.rcolaco.boilerplate.monitor;

import javax.management.MalformedObjectNameException;
import java.util.logging.Logger;

/**
 *
 */
public class TestMonitor extends AbstractMonitor implements TestMonitorMBean
{
    private static final Logger log	= Logger.getLogger(TestMonitor.class.getName());

    private int n = 27;

    public TestMonitor()
    {
        super();
    }

    /**
     * Be sure to call this once at startup
     */
    public static void registerMBeans()
    {
        try
        {
            final TestMonitor tm = new TestMonitor();
            MBeanUtil.register("TestMonitor", tm);
            //MBeanUtil.register("TestMonitor,name=SomeChild", monitor.getChildMonitor());
        }
        catch (MalformedObjectNameException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public final String getReadOnlyText()
    {
        return "Hello World!";
    }

    @Override
    public final int getReadWriteNumeric()
    {
        return n;
    }

    @Override
    public void setReadWriteNumeric(int n)
    {
        this.n = n;
    }

    @Override
    public void doSomething()
    {
        System.exit(0);
    }
}
