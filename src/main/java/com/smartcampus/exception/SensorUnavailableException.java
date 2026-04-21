package com.smartcampus.exception;

// exception when a sensor is offline or unavailable
public class SensorUnavailableException extends RuntimeException {

    public SensorUnavailableException(String message) {
        super(message);
    }
}