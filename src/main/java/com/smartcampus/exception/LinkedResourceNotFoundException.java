package com.smartcampus.exception;

// custom exception for missing linked resources
public class LinkedResourceNotFoundException extends RuntimeException {
    public LinkedResourceNotFoundException(String message) {
        super(message);
    }
}
