package com.catalyst.ProCounsellor.config;

import org.apache.hc.client5.http.config.RequestConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import java.util.concurrent.TimeUnit;

@Configuration
@Slf4j
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate(@Qualifier("restTemplateRequestFactory") HttpComponentsClientHttpRequestFactory requestFactory, ApiErrorHandler apiErrorHandler) {
        log.info("Creating RestTemplate bean with connection pooling and custom error handler.");
        RestTemplate restTemplate = new RestTemplate(requestFactory);
        restTemplate.setErrorHandler(apiErrorHandler);
        return restTemplate;
    }

    @Bean("restTemplateRequestFactory")
    public HttpComponentsClientHttpRequestFactory requestFactory() {
        // 1. Use the HttpClient 5 connection manager
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(100);
        connectionManager.setDefaultMaxPerRoute(20);

        // Configure request timeouts
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(5, TimeUnit.SECONDS)
                .setResponseTimeout(10, TimeUnit.SECONDS)
                .build();

        // 2. Use the HttpClient 5 builder and fix the syntax
        CloseableHttpClient httpClient = HttpClients.custom()
                .disableCookieManagement()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig) // Apply request configuration
                .build();

        log.info("Created HttpClient 5 with connection pool (Max Total: 100, Max Per Route: 20) and timeouts (Connect: 5s, Socket: 10s, Response: 10s).");

        // 3. Spring's factory is compatible with the v5 client
        return new HttpComponentsClientHttpRequestFactory(httpClient);
    }

    @Bean
    public ApiErrorHandler apiErrorHandler() {
        log.debug("Creating ApiErrorHandler bean.");
        return new ApiErrorHandler();
    }

}