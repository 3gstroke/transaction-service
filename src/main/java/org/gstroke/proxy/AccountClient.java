package org.gstroke.proxy;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.gstroke.proxy.dto.AccountResponse;

import java.math.BigDecimal;

@Path("/accounts")
@RegisterRestClient(configKey = "account-service")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface AccountClient {

    @GET
    @Path("/{number}")
    AccountResponse getAccount(@PathParam("number") String number);

    @GET
    @Path("/{accountNumber}/balance/validate")
    boolean validateBalance(@PathParam("accountNumber") String accountNumber,
                            @QueryParam("amount") BigDecimal amount);
}
