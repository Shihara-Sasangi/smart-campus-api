package com.smartcampus.resource;

import com.smartcampus.exception.LinkedResourceNotFoundException;
import com.smartcampus.model.ErrorResponse;
import com.smartcampus.model.Room;
import com.smartcampus.model.Sensor;
import com.smartcampus.repository.Database;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Path("/sensors")
public class SensorResource {
    
    private final Database db = Database.getInstance();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSensors(@QueryParam("type") String type) {
        Collection<Sensor> allSensors = db.getSensors().values();
        
        if (type != null && !type.trim().isEmpty()) {
            List<Sensor> filtered = allSensors.stream()
                .filter(s -> type.equalsIgnoreCase(s.getType()))
                .collect(Collectors.toList());
            return Response.ok(filtered).build();
        }
        
        return Response.ok(allSensors).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createSensor(Sensor sensor) {
        if (sensor.getId() == null || sensor.getId().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity(new ErrorResponse("Bad Request", "Sensor ID is required"))
                           .build();
        }
        
        if (sensor.getRoomId() == null || sensor.getRoomId().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity(new ErrorResponse("Bad Request", "Room ID is required"))
                           .build();
        }

        // Validate that the roomId specified actually exists
        Room room = db.getRooms().get(sensor.getRoomId());
        if (room == null) {
            throw new LinkedResourceNotFoundException("Room with ID " + sensor.getRoomId() + " does not exist.");
        }

        if (db.getSensors().containsKey(sensor.getId())) {
            return Response.status(Response.Status.CONFLICT)
                           .entity(new ErrorResponse("Conflict", "Sensor with this ID already exists"))
                           .build();
        }

        db.getSensors().put(sensor.getId(), sensor);
        
        // Add sensor to the room's list of sensors (synchronize to prevent race conditions on the List)
        synchronized (room.getSensorIds()) {
            room.getSensorIds().add(sensor.getId());
        }
        
        return Response.created(URI.create("/api/v1/sensors/" + sensor.getId()))
                       .entity(sensor)
                       .build();
    }

    // Sub-Resource Locator Pattern
    @Path("/{sensorId}/readings")
    public SensorReadingResource getSensorReadingResource(@PathParam("sensorId") String sensorId) {
        return new SensorReadingResource(sensorId);
    }
}
