package org.gstroke.exposed.web;

import io.smallrye.jwt.build.Jwt;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.time.Duration;
import java.util.Set;

@Path("/jwt")
@Produces(MediaType.TEXT_PLAIN)
public class TokenResource {

    @POST
    @Path("/token/admin")
    public String adminToken() {
        return Jwt.issuer("bank-api")
                .subject("admin")
                .groups(Set.of("ROLE_ADMIN"))
                .expiresIn(Duration.ofHours(1))
                .sign();
    }

    @POST
    @Path("/token/user")
    public String userToken() {
        return Jwt.issuer("bank-api")
                .subject("user")
                .groups(Set.of("ROLE_USER"))
                .expiresIn(Duration.ofHours(1))
                .sign();
    }
}