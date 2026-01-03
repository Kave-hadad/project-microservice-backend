package com.example.auth_service.exception;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import feign.FeignException;
import feign.RetryableException;

@RestControllerAdvice
public class GlobalExceptionHandler {
	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
	    logger.warn("Validation error: {}", ex.getMessage());
	    Map<String, String> errors = new HashMap<>();

	    List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
	    Iterator<FieldError> iterator = fieldErrors.iterator();

	    while (iterator.hasNext()) {
	        FieldError error = iterator.next();
	        errors.put(error.getField(), error.getDefaultMessage());
	        logger.warn("Invalid field: {} - {}", error.getField(), error.getDefaultMessage());
	    }

	    return ResponseEntity.badRequest().body(errors);
	}

	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<String> handleUserNotFound(UserNotFoundException ex) {
	    logger.error("User not found: {}", ex.getMessage(), ex);
	    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<String> handleUnreadableMessage(HttpMessageNotReadableException ex) {
	    logger.warn("Malformed JSON or unreadable input: {}", ex.getMessage());
	    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid input format. Please check.");
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<String> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
	    logger.warn("Unsupported HTTP method: {}", ex.getMessage());
	    return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
	                         .body("HTTP method not supported. Use the correct method.");
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> handleGenericException(Exception ex) {
	    logger.error("Unexpected error: {}", ex.getMessage(), ex);
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error occurred. Please try again later.");
	}

	@ExceptionHandler(DataAccessException.class)
	public ResponseEntity<String> handleDatabaseError(DataAccessException ex) {
	    logger.error("Database error: {}", ex.getMessage(), ex);
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR) .body("Database error occurred. Please try again later.");
	}

	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<String> handleAuthError(AuthenticationException ex) {
	    logger.warn("Authentication failed: {}", ex.getMessage());
	    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");//401
	}

	@ExceptionHandler(FeignException.class)
	public ResponseEntity<String> handleFeignStatusException(FeignException ex) {
	    if (ex.status() >= 400 && ex.status() < 500) {
	        logger.warn("Client error from user-service: {}", ex.getMessage());
	        return ResponseEntity.status(ex.status()).body("Client error when calling user-service");
	    } else if (ex.status() >= 500) {
	        logger.error("Server error from user-service: {}", ex.getMessage(), ex);
	        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("User-service is unavailable. Please try later.");
	    }
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unknown error when calling user-service");
	}

	@ExceptionHandler(RetryableException.class)
	public ResponseEntity<String> handleRetryableException(RetryableException ex) {
	    logger.error("Cannot connect to user-service: {}", ex.getMessage(), ex);
	    return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Cannot connect to user-service.");
	}

	@ExceptionHandler(InvalidTokenException.class)
	public ResponseEntity<String> handleInvalidToken(InvalidTokenException ex) {
	    logger.warn("Invalid token: {}", ex.getMessage());
	    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
	}

	@ExceptionHandler(DuplicateUserException.class)
	public ResponseEntity<String> handleDuplicateUser(DuplicateUserException ex) {
	    logger.warn("Duplicate user: {}", ex.getMessage());
	    return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists"); // 409 Conflict
	}

	@ExceptionHandler(AuthServiceUnavailableException.class)
	public ResponseEntity<String> handleUserServiceUnavailable(AuthServiceUnavailableException ex) {
	    logger.error("User service unavailable: {}", ex.getMessage(), ex);
	    return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("User-service is unavailable. Please try later.");
	}


}
