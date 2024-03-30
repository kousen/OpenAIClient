package com.kousenit.claude.json;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ClaudeRecords {
    public record ClaudeMessageRequest(
            String model,
            @JsonProperty("system") String systemPrompt,
            @JsonProperty("max_tokens") int maxTokens,
            double temperature,
            List<Message> messages
    ) {
    }

    public sealed interface Message
            permits SimpleMessage, TextMessage, MixedContent {
        String role();
    }

    // Message implementations:
    public record SimpleMessage(String role, String content) implements Message { }

    public record TextMessage(String role, List<TextContent> content) implements Message { }

    public record MixedContent(String role, List<Content> content) implements Message { }

    public sealed interface Content
            permits TextContent, ImageContent {
        String type();
    }

    // Content implementations:
    public record TextContent(String type, String text) implements Content { }

    public record ImageContent(String type, ImageSource source) implements Content {
        public record ImageSource(String type,
                                  @JsonProperty("media_type") String mediaType,
                                  String data) {
        }
    }

    // Response records:
    public record ClaudeMessageResponse(
            String id,
            String type,
            String role,
            String model,
            @JsonProperty("stop_reason") String stopReason,
            @JsonProperty("stop_sequence") String stopSequence,
            List<TextContent> content,
            Usage usage) {

        public record Usage(@JsonProperty("input_tokens") int inputTokens,
                            @JsonProperty("output_tokens") int outputTokens) {
        }
    }

}
