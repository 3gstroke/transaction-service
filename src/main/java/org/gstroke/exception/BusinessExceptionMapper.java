package org.gstroke.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.time.Instant;
import java.util.Map;

@Provider
public class BusinessExceptionMapper
        implements ExceptionMapper<BusinessException> {

    @Override
    public Response toResponse(BusinessException exception) {

        HttpStatus status = mapToHttpStatus(exception.getErrorCode());

        return Response.status(status.code)
                .entity(Map.of(
                        "timestamp", Instant.now(),
                        "errorCode", exception.getErrorCode(),
                        "message", exception.getMessage()
                ))
                .build();
    }

    private HttpStatus mapToHttpStatus(ErrorCode code) {
        return switch (code) {
            case CUSTOMER_NOT_FOUND, ACCOUNT_NOT_FOUND -> HttpStatus.NOT_FOUND;
            case CUSTOMER_INACTIVE, SAME_ACCOUNT_TRANSFER -> HttpStatus.CONFLICT;
            case INSUFFICIENT_FUNDS, INVALID_AMOUNT -> HttpStatus.BAD_REQUEST;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }

    private enum HttpStatus {
        BAD_REQUEST(400),
        NOT_FOUND(404),
        CONFLICT(409),
        INTERNAL_SERVER_ERROR(500);

        final int code;

        HttpStatus(int code) {
            this.code = code;
        }
    }
}