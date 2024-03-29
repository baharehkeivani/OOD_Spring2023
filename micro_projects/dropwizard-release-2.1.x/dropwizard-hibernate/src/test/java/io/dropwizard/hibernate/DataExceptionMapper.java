package io.dropwizard.hibernate;

import io.dropwizard.jersey.errors.ErrorMessage;
import org.hibernate.exception.DataException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.Optional;

@Provider
public class DataExceptionMapper implements ExceptionMapper<DataException> {

    @Override
    public Response toResponse(DataException e) {
        final String causeMessage = Optional.ofNullable(e.getCause())
            .map(Throwable::getMessage)
            .orElse("");
        final String message = causeMessage.contains("EMAIL") ? "Wrong email" : "Wrong input";

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ErrorMessage(Response.Status.BAD_REQUEST.getStatusCode(), message))
                .build();
    }
}
