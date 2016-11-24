package com.rcolaco.boilerplate.resource;

import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.rcolaco.boilerplate.model.Status;

@Path("/test")
public class TestResource extends ResourceConfig
{
    @GET
    @Path("/json")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getJson()
    {
        return Response.ok().entity(new Status()).build();
    }
}
