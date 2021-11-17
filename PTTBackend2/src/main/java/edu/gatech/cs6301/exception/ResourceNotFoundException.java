package edu.gatech.cs6301.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException() {
        this("");
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
