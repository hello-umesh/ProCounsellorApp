package com.catalyst.ProCounsellor.exception;

import org.springframework.http.HttpStatus;

import java.io.IOException;

/**
 * A custom runtime exception to be thrown by the ApiErrorHandler,
 * containing the HTTP status and response body from the failed API call.
 */
public class RestTemplateException extends IOException {
    private final HttpStatus statusCode;
    private final String responseBody;

    public RestTemplateException(HttpStatus statusCode, String responseBody) {
        super("API call failed with status " + statusCode + " and body: " + responseBody);
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    public HttpStatus getStatusCode() {
        return statusCode;
    }

    public String getResponseBody() {
        return responseBody;
    }
}