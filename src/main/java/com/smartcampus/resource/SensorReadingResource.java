package com.smartcampus.resource;

import com.smartcampus.exception.SensorUnavailableException;
import com.smartcampus.model.ErrorResponse;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;
import com.smartcampus.repository.Database;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class SensorReadingResource {
    
    private final String sensorId;
    private final Database db = Database.getInstance();

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReadings() {
        // Validate sensor exists
        Sensor sensor = db.getSensors().get(sensorId);
        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                           .entity(new ErrorResponse("Not Found", "Sensor not found"))
                           .build();
        }

        List<SensorReading> readings = db.getSensorReadings().getOrDefault(sensorId, new ArrayList<>());
        return Response.ok(readings).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createReading(SensorReading reading) {
        // Validate sensor exists
        Sensor sensor = db.getSensors().get(sensorId);
        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                           .entity(new ErrorResponse("Not Found", "Sensor not found"))
                           .build();
        }

        // State Constraint: Sensor must not be in MAINTENANCE or OFFLINE
        if ("MAINTENANCE".equalsIgnoreCase(sensor.getStatus()) || "OFFLINE".equalsIgnoreCase(sensor.getStatus())) {
            throw new SensorUnavailableException("Sensor " + sensorId + " is currently " + sensor.getStatus() + " and cannot accept new readings.");
        }

        // Ensure reading has an ID and timestamp if not provided by client
        if (reading.getId() == null) {
            reading = new SensorReading(reading.getValue());
        }

        // Add reading to history
        db.getSensorReadings().computeIfAbsent(sensorId, k -> new ArrayList<>());
        
        synchronized (db.getSensorReadings().get(sensorId)) {
            db.getSensorReadings().get(sensorId).add(reading);
        }

        // Side Effect: Update currentValue on the corresponding parent Sensor object
        sensor.setCurrentValue(reading.getValue());

        return Response.created(URI.create("/api/v1/sensors/" + sensorId + "/readings/" + reading.getId()))
                       .entity(reading)
                       .build();
    }
}
