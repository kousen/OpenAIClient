package com.kousenit.ollamaclient.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

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
}