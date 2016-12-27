package com.rcolaco.boilerplate.resource;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import main.java.com.rcolaco.boilerplate.filter.AuthenticationFilter;
import main.java.com.rcolaco.boilerplate.model.Jwt;
import org.glassfish.jersey.server.ResourceConfig;

import javax.annotation.security.PermitAll;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.rcolaco.boilerplate.model.Status;

import java.util.Optional;

@Singleton
@Path("/test")
@Api(value = "Boilerplate Test", description = "The Boilerplate Test endpoint that interprets all requests.")
public class TestResource extends ResourceConfig
{
    @PermitAll
    @POST
    @Path("/authenticate")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value="Jwt", notes="The jwt wrapper.", response=Jwt.class)
    @ApiResponses(value={
        @ApiResponse(code=401, message="If authentication failed, return an UNAUTHORIZED/401 error.")
    })
    public Response authenticate(
        @ApiParam(value="The username for which the request is being authenticated.", required=true) @FormParam("username") String sUsername,
        @ApiParam(value="The password for which the request is being authenticated.", required=true) @FormParam("password") String sPassword
    )
    {
        if (sUsername.equals("admin") && sPassword.equals("password"))
        {
            try
            {
                Jwt j = new Jwt();
                j.setToken(AuthenticationFilter.getToken(1, 101));
                return Response.ok().entity(j).build();
            } catch (Throwable th)
            {
                th.printStackTrace();
                throw th;
            }
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

    private static final Optional<String> SQS_END_POINT, SQS_QUEUE_URL;
    static {
        SQS_END_POINT = Optional.ofNullable(System.getenv("SQS_END_POINT"));
        SQS_QUEUE_URL = Optional.ofNullable(System.getenv("SQS_QUEUE_URL"));
    }

    @POST
    @Path("/job/dispatch")
    @Produces(MediaType.APPLICATION_JSON)
    public Response dispatchJob()
    {
        AWSCredentials awsc = null;
        try {
            awsc = new EnvironmentVariableCredentialsProvider().getCredentials();
        } catch (Exception e) {
            throw new AmazonClientException(
                "Can't load credentials from the env vars AWS_ACCESS_KEY_ID and AWS_SECRET_ACCESS_KEY.",
                e
            );
        }

        try {
            final AmazonSQSClient sqs = new AmazonSQSClient(awsc);
            sqs.setEndpoint(SQS_END_POINT.orElse("https://sqs.end.point/not/configured"));
            final SendMessageRequest smreq = new SendMessageRequest(
                SQS_QUEUE_URL.orElse("https://sqs.queue.url/not/configured"),
                "Hello World " + System.currentTimeMillis()
            );
            smreq.setMessageGroupId("someGroup1");
            smreq.setMessageDeduplicationId(System.currentTimeMillis() + "");
            final SendMessageResult smres = sqs.sendMessage(smreq);
            return Response.ok().entity(smres).build();
        } catch (Throwable th)
        {
            th.printStackTrace();
            throw th;
        }
    }


}
