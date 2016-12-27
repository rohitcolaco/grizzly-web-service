package com.rcolaco.boilerplate.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;

import javax.annotation.Priority;
import javax.annotation.security.PermitAll;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.lang.reflect.Method;
import java.security.Key;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements javax.ws.rs.container.ContainerRequestFilter
{
    private static final Logger log = Logger.getLogger(AuthenticationFilter.class.getName());

    private static final Key key = MacProvider.generateKey();

    @Context
    private ResourceInfo resourceInfo;

    private static final String COMPANY = "company", RCOLACO = "RCOLACO";

    private static final Response ACCESS_DENIED = Response
        .status(Response.Status.UNAUTHORIZED)
        .entity("You aren't authenticated to access this resource.").build();

    private static final Response ACCESS_FORBIDDEN = Response
        .status(Response.Status.FORBIDDEN)
        .entity("You aren't authorized to access this resource").build();

    @Override
    public void filter(ContainerRequestContext rc)
    {
        final Method method = resourceInfo.getResourceMethod();
        if (method.isAnnotationPresent(PermitAll.class))
        {
            return;
        }

        final String sAuthorization = rc.getHeaderString(HttpHeaders.AUTHORIZATION);
        if (sAuthorization == null || !sAuthorization.startsWith("Bearer "))
        {
            rc.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity("Missing authentication credentials").build());
            return;
        }

        final String sToken = sAuthorization.substring("Bearer".length()).trim();
        log.info("Authorization token is " + sToken);

        try {
            final Claims c = Jwts.parser().setSigningKey(key).parseClaimsJws(sToken).getBody();
            assert c.containsKey(COMPANY) && c.get(COMPANY).equals(RCOLACO);
        }
        catch (Throwable thx)
        {
            log.log(Level.SEVERE, "An unexpected error occurred while interpreting the token", thx);
            rc.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
        }

        // TODO: VALIDATE ROLES?
    }

    /**
     *
     * @param loAccountId
     * @param loUserId
     * @return
     */
    public static final String getToken(long loAccountId, long loUserId)
    {
        final Map<String, Object> mpClaims = new LinkedHashMap<>();
        mpClaims.put("account", loAccountId);
        mpClaims.put("user", loUserId);
        mpClaims.put(COMPANY, RCOLACO);
        return Jwts.builder()
            .setClaims(mpClaims)
            .signWith(SignatureAlgorithm.HS512, key)
            .compact();
    }
}
