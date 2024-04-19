package com.kousenit.stabilityai.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kousenit.openaiclient.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class LoggingInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(LoggingInterceptor.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final int MAX_LOG_LENGTH = 100;

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        if (MediaType.MULTIPART_FORM_DATA.equalsTypeAndSubtype(request.getHeaders().getContentType())) {
            if (!validateMultipartRequest(body)) {
                throw new IllegalArgumentException("Invalid multipart request: Missing required parts or values are not reasonable");
            } else {
                logger.info("Valid multipart request");
            }
        }
        // logRequestBody(request, body);
        ClientHttpResponse response = execution.execute(request, body);
        logResponse(response);
        return response;
    }

    private boolean validateMultipartRequest(byte[] body) {
        // You'll need to parse the byte[] to check the individual parts
        // This is just a simple placeholder logic
        String content = new String(body, StandardCharsets.UTF_8);
        // Here, add logic to validate the presence of "image", "cfg_scale", "motion_bucket_id"
        // This could involve regex checking, JSON parsing, etc., depending on the actual structure of your body
        return content.contains("image") && content.contains("cfg_scale") && content.contains("motion_bucket_id");
    }

    private void logRequestBody(HttpRequest request, byte[] body) {
        String requestBody = new String(body, StandardCharsets.UTF_8);
        // logger.info("Raw Request Body: {}", requestBody);
        try {
            JsonNode jsonNode = objectMapper.readTree(requestBody);
            JsonNode imageNode = jsonNode.path("image");
            String imagePreview = getImagePreview(imageNode);
            logger.info("Request Method: {}, URI: {}", request.getMethod(), request.getURI());
            logger.info("Logged Request Body (image preview): {}", imagePreview);
            logger.info("cfg_scale: {}", jsonNode.path("cfg_scale"));
            logger.info("motion_bucket_id: {}", jsonNode.path("motion_bucket_id"));
        } catch (Exception e) {
            logger.error("Error parsing JSON or logging request: {}", e.getMessage());
        }
    }

    private String getImagePreview(JsonNode imageNode) {
        if (imageNode.isMissingNode() || !imageNode.isTextual()) {
            return "No Image Tag Found or not a text";
        }
        String imageData = imageNode.asText();
        return imageData.substring(0, Math.min(imageData.length(), MAX_LOG_LENGTH)) + "...[TRUNCATED]";
    }

    private void logResponse(ClientHttpResponse response) throws IOException {
        if (logger.isInfoEnabled()) {
            logger.info("Response Status Code: {}", response.getStatusCode());
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(response.getBody(), StandardCharsets.UTF_8));
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
