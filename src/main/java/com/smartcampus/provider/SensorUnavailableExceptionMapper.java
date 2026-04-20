package com.smartcampus.provider;

import com.smartcampus.exception.SensorUnavailableException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import com.smartcampus.model.ErrorResponse;

@Provider
public class SensorUnavailableExceptionMapper implements ExceptionMapper<SensorUnavailableException> {

    @Override
    public Response toResponse(SensorUnavailableException exception) {
        ErrorResponse response = new ErrorResponse("Forbidden", exception.getMessage());

        return Response.status(Response.Status.FORBIDDEN)
                       .entity(response)
                       .type(MediaType.APPLICATION_JSON)
                       .build();
    }
}
