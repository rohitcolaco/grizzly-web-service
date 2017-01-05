package com.rcolaco.boilerplate;

import com.owlike.genson.ext.jaxrs.GensonJsonConverter;
import com.rcolaco.boilerplate.configuration.ConfigurationProvider;
import com.rcolaco.boilerplate.httphandler.FileUploadHandler;
import com.rcolaco.boilerplate.filter.AuthenticationFilter;
import com.rcolaco.boilerplate.monitor.TestMonitor;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.ServerConfiguration;
import org.glassfish.grizzly.http.server.StaticHttpHandler;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class Main
{
    private static final Logger log	= Logger.getLogger(Main.class.getName());

    public static final String BASE_URI = "http://localhost:5000/gws/";

    /**
     * Startup method / entry point
     * @param args
     * @throws Throwable
     */
    public static void main(String[] args) throws Throwable
    {
        if (args.length < 1)
        {
            log.log(Level.SEVERE, "At least one program argument (path to config.yml) needs to be specified.");
            System.exit(0);
        }
        final HttpServer server = startServer(args[0]);
        System.out.println(String.format("Jersey app started with WADL available at "
            + "%sapplication.wadl", BASE_URI));

        final ServerConfiguration sc = server.getServerConfiguration();

        final StaticHttpHandler shhSwagger = new StaticHttpHandler("c:\\code\\personal\\static-web\\public\\swagger-ui");
        sc.addHttpHandler(shhSwagger, "/swagger");

        final FileUploadHandler fuh = new FileUploadHandler();
        sc.addHttpHandler(fuh, "/gws/fileupload");
    }

    /**
     * Callback when the server is started
     * @return
     */
    public static HttpServer startServer(String sPathToConfiguration)
    {
        TestMonitor.registerMBeans();
        final ResourceConfig rc = new ResourceConfig().packages("com.rcolaco.boilerplate.resource");
        rc.register(AuthenticationFilter.class);
        rc.register(GensonJsonConverter.class);
        ConfigurationProvider.instance().load(sPathToConfiguration);
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }
}
