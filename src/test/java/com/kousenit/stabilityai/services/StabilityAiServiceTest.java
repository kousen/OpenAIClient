package com.kousenit.stabilityai.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class StabilityAiServiceTest {

    @Autowired
    private StabilityAiService stabilityAiService;

    @Test
    void requestStabilityAiImage() {
        byte[] bytes = stabilityAiService.requestStabilityAiImage("""
                        cats playing gin rummy
                        """);
        assertThat(bytes).isNotEmpty();
        System.out.println("bytes.length = " + bytes.length);
    }
}