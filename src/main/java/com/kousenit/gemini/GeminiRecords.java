package com.kousenit.gemini;

import java.util.List;

public class GeminiRecords {

    public record CountTokensRequest(
            List<Content> contents,
            GenerateContentRequest generateContentRequest
    ) {}

    // Add GenerateContentRequest record
    public record GenerateContentRequest(
            String model,
            List<Content> contents,
            List<CachedContent.Tool> tools,
            CachedContent.ToolConfig toolConfig,
            List<SafetySetting> safetySettings,
            Content systemInstruction,
            GenerationConfig generationConfig,
            String cachedContent
    ) {}

    public record SafetySetting(
            SafetyCategory category,
            SafetyThreshold threshold
    ) {}

    public enum SafetyCategory {
        HARM_CATEGORY_HATE_SPEECH,
        HARM_CATEGORY_SEXUALLY_EXPLICIT,
        HARM_CATEGORY_DANGEROUS_CONTENT,
        HARM_CATEGORY_HARASSMENT
        // Add other categories if needed
    }

    public enum SafetyThreshold {
        // Add threshold levels as needed
    }

    // Add GenerationConfig record (placeholder)
    public record GenerationConfig(
            // Add fields as needed
    ) {}


    public record GeminiRequest(List<Content> contents, String cachedContent) {}
    public record Content(List<Part> parts, String role) {}

    // "sealed" classes and interfaces:
    // - only "permitted" classes can implement the interface
    //   or extend the class
    public sealed interface Part
            permits TextPart, InlineDataPart {
    }

    public record TextPart(String text) implements Part {}

    public record InlineDataPart(InlineData inlineData) implements Part {}

    public record InlineData(String mimeType, String data) { }

    public record GeminiResponse(
            List<Candidate> candidates,
            PromptFeedback promptFeedback) {
        public record Candidate(
                Content content,
                String finishReason,
                int index,
                List<SafetyRating> safetyRatings) {
            public record Content(List<TextPart> parts, String role) { }
        }
    }

    public record SafetyRating(String category, String probability) { }
    public record PromptFeedback(List<SafetyRating> safetyRatings) { }


    // Returned from "count" endpoint
    public record GeminiCountResponse(int totalTokens) { }

    // Cache request
    public record CachedContent(
            List<Content> contents,
            List<Tool> tools,
            String createTime,  // output only (Timestamp)
            String updateTime,  // output only (Timestamp)
            UsageMetadata usageMetadata,  // output only
            String ttl,  // Duration in seconds, ending in "s", as in "3.5s"
            String name, // optional, format: "cachedContents/{id}"
            String displayName,  // optional
            String model,        // models/{model}
            Content systemInstruction, // optional
            ToolConfig toolConfig  // optional
    ) {
        public record UsageMetadata(int totalTokenCount) { }
        public record Tool(List<FunctionDeclaration> functionDeclarations) {
            public record FunctionDeclaration(String name, String description, List<Parameter> parameters) { }
            public record Parameter(String name, String type) { }
        }
        public record ToolConfig(FunctionCallingConfig functionCallingConfig, Mode mode) {
            public record FunctionCallingConfig(String mode, List<String> allowedFunctionNames) { }
        }

        public enum Mode {
            MODE_UNSPECIFIED, AUTO, ANY, NONE
        }
    }

    public record CachedContentResponse(
            List<CachedContent> cachedContents,
            String nextPageToken
    ) {}

    // Models
    public record ModelList(List<Model> models) {}

    public record Model(
            String name,
            String version,
            String displayName,
            String description,
            int inputTokenLimit,
            int outputTokenLimit,
            List<String> supportedGenerationMethods,
            double temperature,
            double topP,
            int topK
    ) {}
}
