package com.rcolaco.boilerplate.resource;

import main.java.com.rcolaco.boilerplate.filter.AuthenticationFilter;
import main.java.com.rcolaco.boilerplate.model.Jwt;
import org.glassfish.jersey.server.ResourceConfig;

import javax.annotation.security.PermitAll;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.rcolaco.boilerplate.model.Status;

@Path("/test")
public class TestResource extends ResourceConfig
{
    /*
    @PermitAll
    @POST
    @Path("/authenticate")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response authenticate(
        @FormParam("username") String sUsername,
        @FormParam("password") String sPassword
        */
    @PermitAll
    @GET
    @Path("/authenticate")
    @Produces(MediaType.APPLICATION_JSON)
    public Response authenticate(
        @QueryParam("username") String sUsername,
        @QueryParam("password") String sPassword
    )
    {
        if (sUsername.equals("admin") && sPassword.equals("password"))
        {
            Jwt j = new Jwt();
            j.setToken(AuthenticationFilter.getToken(1, 101));
            return Response.ok().entity(j).build();
        }
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }

    @GET
    @Path("/json")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getJson()
    {
        return Response.ok().entity(new Status()).build();
    }
}
