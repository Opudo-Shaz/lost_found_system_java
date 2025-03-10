package com.example.lostandfound;

public class LostItemNotFoundException extends RuntimeException {

    public LostItemNotFoundException(String message) {
        super(message);
    }

    public LostItemNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
