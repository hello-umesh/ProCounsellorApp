package com.catalyst.ProCounsellor.config;

import com.catalyst.ProCounsellor.exception.RestTemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;



public  class ApiErrorHandler extends DefaultResponseErrorHandler {
    private static final Logger log = LoggerFactory.getLogger(RestTemplateConfig.class);

    private String getBodyAsString(ClientHttpResponse response) {
        try (Scanner scanner = new Scanner(response.getBody(), StandardCharsets.UTF_8.name())) {
            return scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
        } catch (IOException e) {
            // Log this exception or handle it as per your application's needs
            return "Could not read response body.";
        }
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        // The default hasError() check ensures this method is only called for 4xx/5xx responses.
        HttpStatusCode statusCode = response.getStatusCode();
        String responseBody = getBodyAsString(response);

        log.warn("API call failed with status code: {} and body: {}", statusCode, responseBody);

        // Throw our custom exception with the status and body
        throw new RestTemplateException(HttpStatus.valueOf(statusCode.value()), responseBody);
    }

}
