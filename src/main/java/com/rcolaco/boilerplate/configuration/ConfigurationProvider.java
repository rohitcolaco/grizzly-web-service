package com.rcolaco.boilerplate.configuration;

import com.esotericsoftware.yamlbeans.YamlReader;

import java.io.File;
import java.io.FileReader;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Loads a configuration yaml and refreshes it every 5 minutes.
 */
public class ConfigurationProvider
{
    private static final Logger log	= Logger.getLogger(ConfigurationProvider.class.getName());

    private static ConfigurationProvider cp = new ConfigurationProvider();

    private static final int RELOAD_INTERVAL_MINUTES = 5;

    private Timer tiReload = null;

    private ReloaderTask rt = null;

    private String sPathToConfiguration = null;

    private long lastModified = 0;

    private Configuration cfg = null;

    /**
     *
     */
    private ConfigurationProvider() { }

    /**
     * @return
     */
    public static ConfigurationProvider instance() { return cp; }

    /**
     *
     * @param sPathToConfiguration
     */
    public final void load(String sPathToConfiguration)
    {
        this.sPathToConfiguration = sPathToConfiguration;
        final File f = new File(sPathToConfiguration);
        if (f.lastModified() != lastModified)
        {
            log.log(Level.INFO, "Loading configuration");
            lastModified = f.lastModified();
            YamlReader reader = null;
            try
            {
                reader = new YamlReader(new FileReader(f));
                cfg = reader.read(Configuration.class);
                if (cfg == null)
                {
                    cfg = new Configuration();
                }

                log.log(Level.INFO, "Configuration loaded successfully from " + sPathToConfiguration);
            }
            catch (Exception e)
            {
                log.log(Level.SEVERE, "Error loading config file from " + sPathToConfiguration, e);
            }
            finally
            {
                try
                {
                    if (reader != null)
                    {
                        reader.close();
                    }
                }
                catch (Exception ex)
                {
                    log.log(Level.WARNING, "Error closing config file " + sPathToConfiguration, ex);
                }
            }
        }
        if (tiReload == null) // SETUP THE FIRST TIME ONLY
        {
            log.info(cfg.toString());
            setupReloader();
        }
    }

    /**
     *
     * @return
     */
    public final Configuration getConfiguration()
    {
        return cfg;
    }

    /**
     *
     */
    private final void setupReloader()
    {
        tiReload = new Timer("Configuration Reloader");
        rt = new ReloaderTask();
        tiReload.schedule(rt,
            TimeUnit.MINUTES.toMillis(RELOAD_INTERVAL_MINUTES),
            TimeUnit.MINUTES.toMillis(RELOAD_INTERVAL_MINUTES)
        );
        addShutdownHook();
    }

    /**
     *
     */
    private void addShutdownHook()
    {
        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run()
            {
                setName("Configuration Provider shutdown hook");
                log.info("Shutdown hook for ConfigurationProvider called");
                ConfigurationProvider.instance().shutdown();
            }
        });
    }

    /**
     *
     */
    public void shutdown()
    {
        try {
            if (rt != null)
            {
                rt.cancel();
            }
            rt = null;
            if (tiReload != null)
            {
                tiReload.cancel();
            }
            tiReload = null;
        }
        catch(Exception ex)
        {
            log.log(Level.SEVERE, "Error shutting down configuration provider", ex);
        }
    }

    /**
     *
     */
    private final class ReloaderTask extends TimerTask
    {
        @Override
        public void run()
        {
            ConfigurationProvider.instance().load(sPathToConfiguration);
        }
    }
}
