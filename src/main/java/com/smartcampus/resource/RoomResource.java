package com.smartcampus.resource;

import com.smartcampus.exception.RoomNotEmptyException;
import com.smartcampus.model.ErrorResponse;
import com.smartcampus.model.Room;
import com.smartcampus.repository.Database;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import java.util.Collection;

@Path("/rooms")
public class RoomResource {
    
    private final Database db = Database.getInstance();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllRooms() {
        Collection<Room> rooms = db.getRooms().values();
        return Response.ok(rooms).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createRoom(Room room) {
        if (room.getId() == null || room.getId().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity(new ErrorResponse("Bad Request", "Room ID is required"))
                           .build();
        }
        
        if (db.getRooms().containsKey(room.getId())) {
            return Response.status(Response.Status.CONFLICT)
                           .entity(new ErrorResponse("Conflict", "Room with this ID already exists"))
                           .build();
        }

        db.getRooms().put(room.getId(), room);
        
        return Response.created(URI.create("/api/v1/rooms/" + room.getId()))
                       .entity(room)
                       .build();
    }

    @GET
    @Path("/{roomId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRoom(@PathParam("roomId") String roomId) {
        Room room = db.getRooms().get(roomId);
        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND)
                           .entity(new ErrorResponse("Not Found", "Room not found"))
                           .build();
        }
        return Response.ok(room).build();
    }

    @DELETE
    @Path("/{roomId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        Room room = db.getRooms().get(roomId);
        
        // If room is not found, returning 404 is a valid approach, 
        // though some idempotent implementations prefer 204 or 200.
        // The coursework specifically notes returning 404 on subsequent requests.
        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND)
                           .entity(new ErrorResponse("Not Found", "Room not found"))
                           .build();
        }

        // Business Logic Constraint: Cannot delete if active sensors are assigned
        if (room.getSensorIds() != null && !room.getSensorIds().isEmpty()) {
            throw new RoomNotEmptyException("Cannot delete room " + roomId + " because it still has sensors assigned to it.");
        }

        db.getRooms().remove(roomId);
        return Response.noContent().build();
    }
}
