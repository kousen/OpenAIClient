package com.kousenit.ollamaclient.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static com.kousenit.ollamaclient.json.OllamaRecords.OllamaGenerateImageRequest;
import static com.kousenit.ollamaclient.json.OllamaRecords.OllamaGenerateTextRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SuppressWarnings("SqlNoDataSourceInspection")
@SpringBootTest
class OllamaServiceTest {
    @Autowired
    private OllamaService service;

    @Test
    void chat() {
        var response = service.chat("orca-mini", "Why is the sky blue?");
        assertFalse(response.isBlank());
        System.out.println(response);
    }

    @Test
    void chatWithDefaultModel() {
        var response = service.chatWithDefaultModel("Why is the sky blue?");
        assertFalse(response.isBlank());
        System.out.println(response);
    }

    @Test
    void conversation() {
        var response = service.conversation("Why is the sky blue?",
                "Because of Rayleigh scattering.",
                "How is that different from Mie scattering?");
        assertFalse(response.isBlank());
        System.out.println(response);
    }

    @Test
    void generateWithText() {
        var response = service.generate(
                new OllamaGenerateTextRequest(
                        "orca-mini", "Why is the sky blue?", false));
        assertFalse(response.isBlank());
        assertThat(response).contains("scattering");
        System.out.println(response);
    }

    @Test
    void generateWithImage() {
        var response = service.generate(
                new OllamaGenerateImageRequest(
                        "llava", "What is in this image?",
                        List.of("src/main/resources/images/happy_leaping_robot.png"),
                        false));
        assertFalse(response.isBlank());
        System.out.println(response);
    }
}