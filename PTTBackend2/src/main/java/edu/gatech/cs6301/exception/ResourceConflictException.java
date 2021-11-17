package edu.gatech.cs6301.exception;

public class ResourceConflictException extends RuntimeException {
    public ResourceConflictException() {
        this("");
    }

    public ResourceConflictException(String message) {
        super(message);
    }
}
