package com.kousenit.stabilityai.services;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StabilityAIRequestTest {
    private final StabilityAIRequest stabilityAI = new StabilityAIRequest();

    @Test
    void requestStableImage() {
        try {
            stabilityAI.requestStableImage("""
                            a photorealistic image of a happy robot jumping on springs,
                            thrilled that he accomplished a hard task""");
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}