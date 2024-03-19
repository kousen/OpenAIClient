package com.kousenit.ollamaclient.json;

import java.util.List;

public class OllamaRecords {
    public record OllamaGenerateRequest(String model,
                                        String prompt,
                                        boolean stream) { }

    public record OllamaGenerateResponse(String model,
                                         String createdAt,
                                         String response,
                                         boolean done,
                                         long totalDuration,
                                         int promptEvalCount,
                                         int evalCount) { }

    public record Message(String role, String content) { }

    public record OllamaChatRequest(String model,
                                    List<Message> messages,
                                    boolean stream) { }

    public record OllamaChatResponse(String model,
                                     String createdAt,
                                     Message message,
                                     boolean done,
                                     long totalDuration,
                                     int promptEvalCount,
                                     int evalCount) { }
}
