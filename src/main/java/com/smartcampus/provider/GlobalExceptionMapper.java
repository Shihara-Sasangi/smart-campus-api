package com.smartcampus.provider;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import com.smartcampus.model.ErrorResponse;

// this catches all unhandled exceptions so the server doesnt crash
@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable exception) {
        // stop leaking stack traces and just return 500 error
        ErrorResponse response = new ErrorResponse("Internal Server Error", "An unexpected server error occurred.");
        
        // normally i would log the real stack trace here

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                       .entity(response)
                       .type(MediaType.APPLICATION_JSON)
                       .build();
    }
}
