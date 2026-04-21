package com.smartcampus.provider;

import com.smartcampus.exception.LinkedResourceNotFoundException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import com.smartcampus.model.ErrorResponse;

// map my linked resource exception to a 422 error
@Provider
public class LinkedResourceNotFoundExceptionMapper implements ExceptionMapper<LinkedResourceNotFoundException> {

    @Override
    public Response toResponse(LinkedResourceNotFoundException exception) {
        // returning 422 unprocessable entity
        ErrorResponse response = new ErrorResponse("Unprocessable Entity", exception.getMessage());

        return Response.status(422)
                       .entity(response)
                       .type(MediaType.APPLICATION_JSON)
                       .build();
    }
}
