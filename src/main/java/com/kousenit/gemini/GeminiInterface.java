package com.kousenit.gemini;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.*;

import static com.kousenit.gemini.GeminiRecords.*;

@HttpExchange("/v1beta")
public interface GeminiInterface {
    @GetExchange("/models")
    ModelList getModels();

    @PostExchange("/models/{model}:generateContent")
    GeminiResponse getCompletion(
            @PathVariable String model,
            @RequestBody GeminiRequest request);

    @PostExchange("/models/{model}:countTokens")
    GeminiCountResponse countTokens(
            @PathVariable String model,
            @RequestBody CountTokensRequest request);

    @PostExchange("cachedContents")
    CachedContent createCachedContents(
            @RequestBody CachedContent request);

    @GetExchange("cachedContents")
    CachedContentResponse listCachedContents();

    @GetExchange("cachedContents/{id}")
    CachedContent getCachedContent(@PathVariable String id);

    @DeleteExchange("cachedContents/{id}")
    void deleteCachedContent(@PathVariable String id);

    @PatchExchange("cachedContents/{id}")
    CachedContent updateCachedContent(
            @PathVariable String id,
            @RequestBody CachedContent request);
}
