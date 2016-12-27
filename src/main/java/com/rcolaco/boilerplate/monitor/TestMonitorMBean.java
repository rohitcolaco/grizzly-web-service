package com.rcolaco.boilerplate.monitor;

/**
 *
 */
public interface TestMonitorMBean
{
    /**
     * A numeric field value to be tested via the jmx console
     */
    int getReadWriteNumeric();

    /**
     * A numeric field value that can be written to
     */
    void setReadWriteNumeric(int n);

    /**
     * A text field value that can be read from
     */
    String getReadOnlyText();

    void doSomething();
}
