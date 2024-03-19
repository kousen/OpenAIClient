package com.kousenit.claude.json;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ClaudeRequest(String model,
                            String prompt,
                            int maxTokensToSample,
                            double temperature
) {
}
