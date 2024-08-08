package com.kousenit.openaiclient.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.kousenit.openaiclient.services.DallEService;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

public class OpenAIRecords {

    // For chat requests and responses
    public record ChatRequest(
            String model,
            List<Message> messages,
            double temperature) {
    }

    public record Message(Role role, String content) {
    }

    public record ChatResponse(
            String id,
            String object,
            long created,
            String model,
            Usage usage,
            List<Choice> choices
    ) {
        @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
        public record Usage(
                int promptTokens,
                int completionTokens,
                int totalTokens) {
        }

        @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
        public record Choice(
                Message message,
                String finishReason,
                int index) {
        }
    }

    // For models
    public record ModelList(List<Model> data) {
        @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
        public record Model(String id, long created, String ownedBy) {

            @Override
            public String toString() {
                return "Model{id='%s', created=%s, ownedBy='%s'}".formatted(
                        id, Instant.ofEpochSecond(created)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDateTime(),
                        ownedBy);
            }
        }
    }

    // For generating images
    public record Image(String b64_json) {
    }

    public record ImageRequest(
            @Pattern(regexp = "dall-e-[23]") String model,
            @Size(max = 4096) String prompt,
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

    public record ImageResponse(
            Long created,
            List<Image> data) {
    }

}
