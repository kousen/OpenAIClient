package com.kousenit.claude.services;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import static com.kousenit.claude.json.ClaudeRecords.ClaudeMessageRequest;
import static com.kousenit.claude.json.ClaudeRecords.ClaudeMessageResponse;

@HttpExchange("/v1")
public interface ClaudeInterface {
    @PostExchange("/messages")
    ClaudeMessageResponse getMessageResponse(@RequestBody ClaudeMessageRequest request);
}
