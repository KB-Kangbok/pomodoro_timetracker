package com.cs6301.PTTBackend2.exception;

public class InvalidRequestBodyException extends RuntimeException {
    public InvalidRequestBodyException() { this(""); }
    public InvalidRequestBodyException(String message) { super(message); }

}
