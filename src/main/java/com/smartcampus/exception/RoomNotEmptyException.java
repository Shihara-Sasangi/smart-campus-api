package com.smartcampus.exception;

// custom exception for when a room is not empty
public class RoomNotEmptyException extends RuntimeException {

    public RoomNotEmptyException(String message) {
        super(message);
    }
}