package com.rcolaco.boilerplate;

import com.owlike.genson.ext.jaxrs.GensonJsonConverter;
import main.java.com.rcolaco.boilerplate.filter.AuthenticationFilter;
import main.java.com.rcolaco.boilerplate.monitor.TestMonitor;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.StaticHttpHandler;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.net.URI;

/**
 *
 */
public class Main
{
    public static final String BASE_URI = "http://localhost:5000/gws/";

    /**
     * Startup method / entry point
     * @param args
     * @throws Throwable
     */
    public static void main(String[] args) throws Throwable
    {
        final HttpServer server = startServer();
        System.out.println(String.format("Jersey app started with WADL available at "
            + "%sapplication.wadl", BASE_URI));

        server.getServerConfiguration().addHttpHandler(new StaticHttpHandler("c:\\code\\personal\\static-web\\public\\swagger-ui"), "/swagger");
    }

    /**
     * Callback when the server is started
     * @return
     */
    public static HttpServer startServer()
    {
        TestMonitor.registerMBeans();
        final ResourceConfig rc = new ResourceConfig().packages("com.rcolaco.boilerplate.resource");
        rc.register(AuthenticationFilter.class);
        rc.register(GensonJsonConverter.class);
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }
}
