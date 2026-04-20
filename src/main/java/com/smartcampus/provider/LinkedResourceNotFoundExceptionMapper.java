package com.smartcampus.provider;

import com.smartcampus.exception.LinkedResourceNotFoundException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import com.smartcampus.model.ErrorResponse;

@Provider
public class LinkedResourceNotFoundExceptionMapper implements ExceptionMapper<LinkedResourceNotFoundException> {

    @Override
    public Response toResponse(LinkedResourceNotFoundException exception) {
        ErrorResponse response = new ErrorResponse("Unprocessable Entity", exception.getMessage());

        return Response.status(422)
                       .entity(response)
                       .type(MediaType.APPLICATION_JSON)
                       .build();
    }
}
