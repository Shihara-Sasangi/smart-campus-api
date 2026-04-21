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
        // check if sensor actually exists
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
        // check if sensor exists again
        Sensor sensor = db.getSensors().get(sensorId);
        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                           .entity(new ErrorResponse("Not Found", "Sensor not found"))
                           .build();
        }

        // sensors cant accept readings if they are offline or broken
        if ("MAINTENANCE".equalsIgnoreCase(sensor.getStatus()) || "OFFLINE".equalsIgnoreCase(sensor.getStatus())) {
            throw new SensorUnavailableException("Sensor " + sensorId + " is currently " + sensor.getStatus() + " and cannot accept new readings.");
        }

        // if client didnt send id, we generate it
        if (reading.getId() == null) {
            reading = new SensorReading(reading.getValue());
        }

        // storing the reading in history
        db.getSensorReadings().computeIfAbsent(sensorId, k -> new ArrayList<>());
        
        synchronized (db.getSensorReadings().get(sensorId)) {
            db.getSensorReadings().get(sensorId).add(reading);
        }

        // update the latest value on the sensor itself
        sensor.setCurrentValue(reading.getValue());

        return Response.created(URI.create("/api/v1/sensors/" + sensorId + "/readings/" + reading.getId()))
                       .entity(reading)
                       .build();
    }
}
