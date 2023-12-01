package com.kousenit.openaiclient.services;

import com.kousenit.openaiclient.json.ClaudeRequest;
import org.springframework.stereotype.Service;

@Service
public class ClaudeService {
    public final static String CLAUDE_2 = "claude-2";

    private final ClaudeInterface claudeInterface;

    public ClaudeService(ClaudeInterface claudeInterface) {
        this.claudeInterface = claudeInterface;
    }

    public String getClaudeResponse(String prompt) {
        ClaudeRequest request = new ClaudeRequest(CLAUDE_2,
                formatPrompt(prompt),
                256, 0.7);
        return claudeInterface.getCompletion(request).completion();
    }

    private String formatPrompt(String prompt) {
        return "\n\nHuman: %s\n\nAssistant:".formatted(prompt);
    }
}
