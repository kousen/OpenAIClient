package com.kousenit.ollamaclient.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

import java.util.List;

import static com.kousenit.ollamaclient.json.OllamaRecords.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
class OllamaInterfaceTest {

    @Autowired
    private OllamaInterface ollamaInterface;

    @Test
    void asycChat() {
        var request = new OllamaChatRequest("orca-mini",
                List.of(new Message("user", "Why is the sky blue?")),
                true);
        Flux<String> response = ollamaInterface.asyncChat(request);
        response.subscribe(new Subscriber<>() {
            private Subscription subscription;

            @Override
            public void onSubscribe(Subscription s) {
                this.subscription = s;
            }

            @Override
            public void onNext(String ollamaStreamingChatResponse) {
                System.out.println(ollamaStreamingChatResponse);
                subscription.request(1);
            }

            @Override
            public void onError(Throwable t) {
                System.err.println(t.getMessage());
            }

            @Override
            public void onComplete() {
                System.out.println("Done");
            }
        });
    }

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
    @ValueSource(strings = {"orca-mini", "llama3", "gemma"})
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