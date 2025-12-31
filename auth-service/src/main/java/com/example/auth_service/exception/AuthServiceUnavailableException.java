package com.example.auth_service.exception;


public class AuthServiceUnavailableException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public AuthServiceUnavailableException(String message) {
        super(message);
    }

    public AuthServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
