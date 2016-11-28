package com.rcolaco.boilerplate;

import main.java.com.rcolaco.boilerplate.filter.AuthenticationFilter;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.net.URI;

/**
 *
 */
public class Main
{
    public static final String BASE_URI = "http://localhost:5000/gws/";

    public static HttpServer startServer()
    {
        final ResourceConfig rc = new ResourceConfig().packages("com.rcolaco.boilerplate.resource");
        rc.register(AuthenticationFilter.class);
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    public static void main(String[] args) throws Throwable
    {
        final HttpServer server = startServer();
        System.out.println(String.format("Jersey app started with WADL available at "
            + "%sapplication.wadl", BASE_URI));
        Thread.currentThread().join();
    }
}
