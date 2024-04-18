package com.kousenit.stabilityai.util;

import com.kousenit.openaiclient.util.FileUtils;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class LoggingInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(LoggingInterceptor.class);

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        logRequest(request, body);
        ClientHttpResponse response = execution.execute(request, body);
        logResponse(response);
        return response;
    }

    private void logRequest(HttpRequest request, byte[] body) {
        if (logger.isInfoEnabled()) {
            logger.info("Request Method: {}", request.getMethod());
            logger.info("Request URI: {}", request.getURI());
            logger.info("Request Body: {}", new String(body, StandardCharsets.UTF_8));
        }
    }

    private void logResponse(ClientHttpResponse response) throws IOException {
        if (logger.isInfoEnabled()) {
            logger.info("Response Status Code: {}", response.getStatusCode());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getBody(), StandardCharsets.UTF_8));
            String line;
            StringBuilder responseData = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                responseData.append(line);
            }
            logger.info("Response Body: {}", responseData);
            // log the response data to a file
            FileUtils.writeResponseDataToFile(responseData.toString());

        }
    }
}
