package com.kousenit.ollamaclient.json;

import com.kousenit.ollamaclient.utils.FileUtils;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class OllamaRecords {

    // Response from listing the local models
    public record ModelList(List<OllamaModel> models) {

        public record OllamaModel(
                String name,
                OffsetDateTime modifiedAt,
                long size,
                String digest,
                Details details
        ) {}

        public record Details(
                String format,
                String family,
                List<String> families, // This can be null, so it's represented as a list that might be empty or null
                String parameterSize,
                String quantizationLevel
        ) {}
    }

    // Records to generate text or images
    public sealed interface OllamaGenerateRequest
            permits OllamaGenerateTextRequest, OllamaGenerateImageRequest {
        String model();
        String prompt();
        boolean stream();
    }

    public record OllamaGenerateTextRequest(
            String model,
            String prompt,
            boolean stream)
            implements OllamaGenerateRequest {
    }

    public record OllamaGenerateImageRequest(
            String model,
            String prompt,
            List<String> images,
            boolean stream)
            implements OllamaGenerateRequest {

        // Transform file names into base64-encoded strings
        public OllamaGenerateImageRequest {
            images = images.stream()
                    .map(image -> isBase64Encoded(image) ? image : FileUtils.encodeImage(image))
                    .collect(Collectors.toList());
        }

        private boolean isBase64Encoded(String image) {
            // Implement Base64 format validation.
            return image.matches("^(?:[A-Za-z0-9+/]{4})*(?:[A-Za-z0-9+/]{2}==|[A-Za-z0-9+/]{3}=)?$");
        }
    }

    public record OllamaGenerateResponse(
            String model,
            String createdAt,
            String response,
            boolean done,
            long totalDuration,
            int promptEvalCount,
            int evalCount) {
    }

    public record Message(String role, String content) {
    }

    public record OllamaChatRequest(
            String model,
            List<Message> messages,
            boolean stream) {
    }

    public record OllamaChatResponse(
            String model,
            String createdAt,
            Message message,
            boolean done,
            long totalDuration,
            int promptEvalCount,
            int evalCount) {
    }
}
