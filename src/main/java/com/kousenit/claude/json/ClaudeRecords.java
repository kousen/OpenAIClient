package com.kousenit.claude.json;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ClaudeRecords {
    public sealed interface Message permits SimpleMessage, TextMessage, MixedContent {
    }

    public record ClaudeMessageRequest(
            String model,
            @JsonProperty("system") String systemPrompt,
            @JsonProperty("max_tokens") int maxTokens,
            double temperature,
            List<Message> messages
    ) {
    }

    public sealed interface Content permits TextContent, ImageContent {
    }

    public record SimpleMessage(String role, String content) implements Message {
    }

    public record TextContent(String type, String text) implements Content {
    }

    public record TextMessage(String role, List<TextContent> content) implements Message {
    }

    public record ImageSource(String type,
                              @JsonProperty("media_type") String mediaType,
                              String data) {
    }

    public record ImageContent(String type, ImageSource source) implements Content {
    }

    public record MixedContent(String role, List<Content> content) implements Message {
    }

    public record ClaudeMessageResponse(
            String id,
            String type,
            String role,
            String model,
            @JsonProperty("stop_reason") String stopReason,
            @JsonProperty("stop_sequence") String stopSequence,
            List<Content> content,
            Usage usage) {
        public record Content(String type, String text) {
        }

        public record Usage(@JsonProperty("input_tokens") int inputTokens,
                            @JsonProperty("output_tokens") int outputTokens) {
        }
    }

}
