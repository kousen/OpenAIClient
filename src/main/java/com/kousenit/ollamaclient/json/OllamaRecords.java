package com.kousenit.ollamaclient.json;

import com.kousenit.ollamaclient.utils.ImageUtils;

import java.util.List;
import java.util.stream.Collectors;

public class OllamaRecords {

    public sealed interface OllamaGenerateRequest
            permits OllamaGenerateTextRequest, OllamaGenerateImageRequest {
        String model();
        String prompt();
        boolean stream();
    }

    public record OllamaGenerateTextRequest(String model,
                                            String prompt,
                                            boolean stream)
            implements OllamaGenerateRequest {
    }

    public record OllamaGenerateImageRequest(String model,
                                             String prompt,
                                             List<String> images,
                                             boolean stream)
            implements OllamaGenerateRequest {

        public OllamaGenerateImageRequest {
            images = images.stream()
                    .map(image -> isBase64Encoded(image) ? image : ImageUtils.encodeImage(image))
                    .collect(Collectors.toList());
        }

        private boolean isBase64Encoded(String image) {
            // Implement Base64 format validation.
            return image.matches("^(?:[A-Za-z0-9+/]{4})*(?:[A-Za-z0-9+/]{2}==|[A-Za-z0-9+/]{3}=)?$");
        }
    }

    public record OllamaGenerateResponse(String model,
                                         String createdAt,
                                         String response,
                                         boolean done,
                                         long totalDuration,
                                         int promptEvalCount,
                                         int evalCount) {
    }

    public record Message(String role, String content) {
    }

    public record OllamaChatRequest(String model,
                                    List<Message> messages,
                                    boolean stream) {
    }

    public record OllamaChatResponse(String model,
                                     String createdAt,
                                     Message message,
                                     boolean done,
                                     long totalDuration,
                                     int promptEvalCount,
                                     int evalCount) {
    }
}
