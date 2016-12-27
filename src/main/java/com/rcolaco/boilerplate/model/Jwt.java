package com.rcolaco.boilerplate.model;

/**
 *
 */
public class Jwt
{
    private String token = "header.payload.signature";

    public Jwt() {}

    public String getToken()
    {
        return token;
    }

    public void setToken(String token)
    {
        this.token = token;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Jwt jwt = (Jwt) o;

        return token != null ? token.equals(jwt.token) : jwt.token == null;

    }

    @Override
    public int hashCode()
    {
        return token != null ? token.hashCode() : 0;
    }

    @Override
    public String toString()
    {
        return "Jwt{" +
            "token='" + token + '\'' +
            '}';
    }
}
