package org.gstroke.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class GlobalExceptionMapper
        implements ExceptionMapper<RuntimeException> {

    @Override
    public Response toResponse(RuntimeException e) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(e.getMessage())
                .build();
    }
}