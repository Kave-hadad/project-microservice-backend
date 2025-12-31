package com.example.user_service.exception;

public class NullInputException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public NullInputException(String message) {
        super(message);
    }
}

