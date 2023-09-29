package com.kousenit.openaiclient.json;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

public record ChatResponse(
        String id,
        String object,
        long created,
        String model,
        Usage usage,
        List<Choice> choices
) {
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record Usage(int promptTokens, int completionTokens, int totalTokens) {}

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record Choice(Message message, String finishReason, int index) {}
}
