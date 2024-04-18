package com.kousenit.stabilityai.services;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StabilityAIRequestTest {
    private final StabilityAIRequest stabilityAI = new StabilityAIRequest();

    @Test
    void requestStableImage() {
        try {
            stabilityAI.requestStableImage("""
                            cats playing gin rummy
                            """);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}