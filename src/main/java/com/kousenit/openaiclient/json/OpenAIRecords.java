package com.kousenit.openaiclient.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.kousenit.openaiclient.services.DallEService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

public class OpenAIRecords {

    // Request records
    public record ChatRequest(
            String model,
            @JsonProperty("max_tokens") int maxTokens,
            double temperature,
            List<Message> messages
    ) {}

    public record Message(Role role, Content content) {}

    public sealed interface Content permits SimpleTextContent, ComplexContent {}

    public record SimpleTextContent(String text) implements Content {
        @Override
        public String toString() {
            return text;
        }
    }

    public record ComplexContent(List<ComplexContentType> contentObjects)
            implements Content {}

    public sealed interface ComplexContentType permits Text, ImageUrl {}

    public record Text(String type, String text) implements ComplexContentType {
        public Text(String text) {
            this("text", text);
        }
    }

    public record ImageUrl(String type,
                           @JsonProperty("image_url") Url imageUrl)
            implements ComplexContentType {
        public ImageUrl(String url) {
            this("image_url", new Url(url));
        }
    }

    public record Url(String url) {}

    // Response records
    public record ChatResponse(
            String id,
            String object,
            long created,
            String model,
            Usage usage,
            List<Choice> choices,
            String system_fingerprint
    ) {
        @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
        public record Usage(int promptTokens, int completionTokens, int totalTokens) {}

        @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
        public record Choice(ResponseMessage message, String finishReason, int index) {}

        public record ResponseMessage(String role, String content) {}
    }

    // For models
    public record ModelList(List<Model> data) {
        @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
        public record Model(String id, long created, String ownedBy) {

            @Override
            public String toString() {
                return "Model{id='%s', created=%s, ownedBy='%s'}".formatted(
                        id, LocalDateTime.ofInstant(
                                Instant.ofEpochSecond(created), ZoneId.systemDefault()),
                        ownedBy);
            }
        }
    }

    // For generating images
    public record Image(String b64_json) {
    }

    public record ImageRequest(
            @NotBlank @Pattern(regexp = "dall-e-[23]") String model,
            @Size(max = 1000) String prompt,
            int n,          // must be 1 for DALL-E-3
            String quality, // "standard" or "hd" for DALL-E-3
            String size,    // 1024x1024 or higher for DALL-E-3
            @JsonProperty("response_format") String responseFormat) {

        public ImageRequest {
            if (model.equals(DallEService.DALL_E_3) && n != 1) {
                throw new IllegalArgumentException("DALL-E-3 only supports n = 1");
            }
        }
    }

    public record ImageResponse(Long created,
                                List<Image> data) {
    }

}
