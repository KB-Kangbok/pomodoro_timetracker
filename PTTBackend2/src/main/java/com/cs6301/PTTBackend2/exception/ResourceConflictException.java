package com.cs6301.PTTBackend2.exception;

public class ResourceConflictException extends RuntimeException {
    public ResourceConflictException() {
        this("");
    }

    public ResourceConflictException(String message) {
        super(message);
    }
}
