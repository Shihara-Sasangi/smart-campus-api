package com.smartcampus.repository;

import com.smartcampus.model.Room;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Singleton class to act as an in-memory database using Maps and Lists.
 * ConcurrentHashMap is used to ensure thread safety across JAX-RS requests.
 */
public class Database {
    private static Database instance;
    
    // In-memory data stores
    private final Map<String, Room> rooms = new ConcurrentHashMap<>();
    private final Map<String, Sensor> sensors = new ConcurrentHashMap<>();
    private final Map<String, List<SensorReading>> sensorReadings = new ConcurrentHashMap<>();

    private Database() {
        // Pre-populate with some initial data for easier testing
        Room room1 = new Room("LIB-301", "Library Quiet Study", 50);
        rooms.put(room1.getId(), room1);
        
        Room room2 = new Room("LAB-101", "Computer Science Lab", 30);
        rooms.put(room2.getId(), room2);
    }

    public static synchronized Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    public Map<String, Room> getRooms() {
        return rooms;
    }

    public Map<String, Sensor> getSensors() {
        return sensors;
    }

    public Map<String, List<SensorReading>> getSensorReadings() {
        return sensorReadings;
    }
}
