package com.smartcampus.repository;

import com.smartcampus.model.Room;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// singleton database class using in-memory maps
// concurrent hashmap to make it thread safe
public class Database {
    private static Database instance;
    
    // where i store my data
    private final Map<String, Room> rooms = new ConcurrentHashMap<>();
    private final Map<String, Sensor> sensors = new ConcurrentHashMap<>();
    private final Map<String, List<SensorReading>> sensorReadings = new ConcurrentHashMap<>();

    private Database() {
        // adding some dummy data to test
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
