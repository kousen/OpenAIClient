package com.kousenit.ollamaclient.services;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import reactor.core.publisher.Flux;

import static com.kousenit.ollamaclient.json.OllamaRecords.*;

@HttpExchange("/api")
public interface OllamaInterface {

    @GetExchange("/tags")
    ModelList getModels();

    @PostExchange("/generate")
    OllamaGenerateResponse generate(@RequestBody OllamaGenerateRequest question);

//    @PostExchange("/generate")
//    OllamaGenerateResponse generate(@RequestBody OllamaGenerateTextRequest question);
//
//    @PostExchange("/generate")
//    OllamaGenerateResponse generate(@RequestBody OllamaGenerateImageRequest question);


    @PostExchange("/chat")
    OllamaChatResponse chat(@RequestBody OllamaChatRequest question);

    @PostExchange("/chat")
    Flux<String> asyncChat(@RequestBody OllamaChatRequest question);
}
