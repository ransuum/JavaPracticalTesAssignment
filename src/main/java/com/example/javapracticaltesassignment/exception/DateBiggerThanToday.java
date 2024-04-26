package com.example.javapracticaltesassignment.exception;

public class DateBiggerThanToday extends  RuntimeException {
    public DateBiggerThanToday(String message) {
        super(message);
    }

    public DateBiggerThanToday(String message, Throwable cause) {
        super(message, cause);
    }
}
