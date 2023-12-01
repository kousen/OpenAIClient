package com.kousenit.openaiclient.services;

import com.kousenit.openaiclient.json.ClaudeRequest;
import com.kousenit.openaiclient.json.ClaudeResponse;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange("/v1/complete")
public interface ClaudeInterface {
    @PostExchange
    ClaudeResponse getCompletion(@RequestBody ClaudeRequest request);
}
