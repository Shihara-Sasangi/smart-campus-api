package com.smartcampus.provider;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import com.smartcampus.model.ErrorResponse;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable exception) {
        // Prevent leaking stack traces by catching all unhandled exceptions
        // and returning a generic 500 Internal Server Error message
        ErrorResponse response = new ErrorResponse("Internal Server Error", "An unexpected server error occurred.");
        
        // In a real production system, we would also log the actual stack trace here internally
        // e.g., Logger.getLogger(GlobalExceptionMapper.class.getName()).log(Level.SEVERE, "Unhandled exception", exception);

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                       .entity(response)
                       .type(MediaType.APPLICATION_JSON)
                       .build();
    }
}
