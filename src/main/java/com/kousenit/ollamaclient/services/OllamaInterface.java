package com.kousenit.ollamaclient.services;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import static com.kousenit.ollamaclient.json.OllamaRecords.*;

@HttpExchange("/api")
public interface OllamaInterface {

    @PostExchange("/chat")
    OllamaChatResponse chat(@RequestBody OllamaChatRequest question);

    @PostExchange("/generate")
    OllamaGenerateResponse generate(@RequestBody OllamaGenerateRequest question);
}
