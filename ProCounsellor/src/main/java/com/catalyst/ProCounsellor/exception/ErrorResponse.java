package com.catalyst.ProCounsellor.exception;

import org.springframework.http.HttpStatus;

import java.time.Instant;

/**
 * A simple POJO to structure our error responses consistently.
 */
public class ErrorResponse {
    private final int statusCode;
    private final String message;
    private final String error;
    private final long timestamp;

    public ErrorResponse(HttpStatus status, String message) {
        this.statusCode = status.value();
        this.message = message;
        this.error = status.getReasonPhrase();
        this.timestamp = Instant.now().toEpochMilli();
    }

    // Getters are needed for Jackson to serialize the object to JSON
    public int getStatusCode() { return statusCode; }
    public String getMessage() { return message; }
    public String getError() { return error; }
    public long getTimestamp() { return timestamp; }
}
