package com.kousenit.claude.services;

import com.kousenit.claude.json.ClaudeRequest;
import com.kousenit.claude.json.ClaudeResponse;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange("/v1/complete")
public interface ClaudeInterface {
    @PostExchange
    ClaudeResponse getCompletion(@RequestBody ClaudeRequest request);
}
