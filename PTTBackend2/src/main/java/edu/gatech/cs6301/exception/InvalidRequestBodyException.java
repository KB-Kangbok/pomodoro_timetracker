package edu.gatech.cs6301.exception;

public class InvalidRequestBodyException extends RuntimeException {
    public InvalidRequestBodyException() { this(""); }
    public InvalidRequestBodyException(String message) { super(message); }

}
