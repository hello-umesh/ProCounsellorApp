package com.catalyst.ProCounsellor.exception.handler;

import com.catalyst.ProCounsellor.exception.ErrorResponse;
import com.catalyst.ProCounsellor.exception.InvalidCredentialsException;
import com.catalyst.ProCounsellor.exception.RestTemplateException;
import com.catalyst.ProCounsellor.exception.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    /**
     * Handles UserNotFoundException, returning a 404 Not Found.
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException ex, WebRequest request) {
        log.warn("User not found for request {}: {}", request.getDescription(false), ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles InvalidCredentialsException, returning a 401 Unauthorized.
     */
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentialsException(InvalidCredentialsException ex, WebRequest request) {
        log.warn("Invalid credentials for request {}: {}", request.getDescription(false), ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handles RestTemplateException, propagating the status and message from the downstream API call.
     * This is useful for proxying errors from services like Twilio.
     */
    @ExceptionHandler(RestTemplateException.class)
    public ResponseEntity<ErrorResponse> handleRestTemplateException(RestTemplateException ex, WebRequest request) {
        HttpStatus status = ex.getStatusCode();
        String errorMessage = "Error from downstream service: " + ex.getResponseBody();
        log.warn("Downstream API call failed for request {}: Status {}, Body: {}", request.getDescription(false), status, ex.getResponseBody());
        ErrorResponse errorResponse = new ErrorResponse(status, errorMessage);
        return new ResponseEntity<>(errorResponse, status);
    }

    /**
     * Handles IllegalStateException, often caused by configuration issues, returning a 500 Internal Server Error.
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException ex, WebRequest request) {
        log.error("Critical configuration or state error for request {}: {}", request.getDescription(false), ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Server configuration error. Please contact support.");
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * A catch-all handler for any other unhandled exceptions.
     * This is crucial to prevent leaking stack traces to the client.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, WebRequest request) {
        log.error("An unexpected error occurred for request {}: {}", request.getDescription(false), ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected internal error occurred.");
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}