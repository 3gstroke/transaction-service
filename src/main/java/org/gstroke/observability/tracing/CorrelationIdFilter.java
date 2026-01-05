package org.gstroke.observability.tracing;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;
import org.slf4j.MDC;

import java.io.IOException;
import java.util.UUID;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class CorrelationIdFilter
        implements ContainerRequestFilter {

    public static final String HEADER = "X-Correlation-Id";

    @Override
    public void filter(ContainerRequestContext request) throws IOException {
        String cid = request.getHeaderString(HEADER);
        if (cid == null) {
            cid = UUID.randomUUID().toString();
        }
        MDC.put(HEADER, cid);
    }

}