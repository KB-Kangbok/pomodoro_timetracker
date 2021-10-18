package com.cs6301.PTTBackend2.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException() {
        this("");
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
