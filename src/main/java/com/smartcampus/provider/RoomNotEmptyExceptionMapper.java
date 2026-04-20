package com.smartcampus.provider;

import com.smartcampus.exception.RoomNotEmptyException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import com.smartcampus.model.ErrorResponse;

@Provider
public class RoomNotEmptyExceptionMapper implements ExceptionMapper<RoomNotEmptyException> {

    @Override
    public Response toResponse(RoomNotEmptyException exception) {
        ErrorResponse response = new ErrorResponse("Conflict", exception.getMessage());

        return Response.status(Response.Status.CONFLICT)
                       .entity(response)
                       .type(MediaType.APPLICATION_JSON)
                       .build();
    }
}
