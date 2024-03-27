package com.kousenit.ollamaclient.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static com.kousenit.ollamaclient.json.OllamaRecords.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OllamaInterfaceTest {

    @Autowired
    private OllamaInterface ollamaInterface;

    @Test
    void generate() {
        var request = new OllamaGenerateTextRequest("orca-mini",
                "Why is the sky blue?",
                false);
        var response = ollamaInterface.generate(request);
        assertEquals("orca-mini", response.model());
        assertFalse(response.response().isBlank());
        System.out.println(response);
    }

    @Test
    void chat() {
        var request = new OllamaChatRequest("orca-mini",
                List.of(new Message("user", "Why is the sky blue?")),
                false);
        var response = ollamaInterface.chat(request);
        assertEquals("orca-mini", response.model());
        assertEquals("assistant", response.message().role());
        assertFalse(response.message().content().isBlank());
        System.out.println(response);
    }

    @ParameterizedTest(name = "model = {0}")
    @ValueSource(strings = {"orca-mini", "llama2", "gemma"})
    void multipleModels(String model) {
        var request = new OllamaGenerateTextRequest(model,
                "Why is the sky blue?",
                false);
        var response = ollamaInterface.generate(request);
        assertEquals(model, response.model());
        assertFalse(response.response().isBlank());
        System.out.println(response);
    }

    @Test
    void conversation() {
        var request = new OllamaChatRequest("orca-mini",
                List.of(new Message("user", "Why is the sky blue?"),
                        new Message("assistant", "Because of Rayleigh scattering."),
                        new Message("user", "How is that different from Mie scattering?")),
                false);
        var response = ollamaInterface.chat(request);
        assertEquals("orca-mini", response.model());
        assertEquals("assistant", response.message().role());
        assertFalse(response.message().content().isBlank());
        System.out.println(response);
    }
}