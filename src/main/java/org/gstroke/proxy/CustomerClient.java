package org.gstroke.proxy;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "customer-service")
@Path("/customers")
public interface CustomerClient {

    @GET
    @Path("/{id}/validate")
    Response validateCustomer(@PathParam("id") Long id);
}