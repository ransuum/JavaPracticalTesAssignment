package com.example.javapracticaltesassignment.exception;

public class LessThanMinAgeException extends RuntimeException {
    public LessThanMinAgeException(String message) {
        super(message);
    }

    public LessThanMinAgeException(String message, Throwable cause) {
        super(message, cause);
    }
}
