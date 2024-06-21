package com.kousenit.claude.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.kousenit.claude.json.ClaudeRecords.*;

@Service
public class ClaudeService {
    public static final Logger logger = LoggerFactory.getLogger(ClaudeService.class);
    public static final Double DEFAULT_TEMPERATURE = 0.3;
    public static final Integer MAX_TOKENS_TO_SAMPLE = 1024;

    public final static String CLAUDE_3_HAIKU = "claude-3-haiku-20240307";
    public final static String CLAUDE_3_SONNET = "claude-3-sonnet-20240229";
    public final static String CLAUDE_35_SONNET = "claude-3-5-sonnet-20240620";
    public final static String CLAUDE_3_OPUS = "claude-3-opus-20240229";

    private final ClaudeInterface claudeInterface;

    public ClaudeService(ClaudeInterface claudeInterface) {
        this.claudeInterface = claudeInterface;
    }

    public String getClaudeMessageResponse(String prompt, String model) {
        ClaudeMessageRequest request = new ClaudeMessageRequest(
                model,
                "",
                MAX_TOKENS_TO_SAMPLE,
                DEFAULT_TEMPERATURE,
                List.of(new SimpleMessage("user", prompt))
        );
        return getClaudeMessageResponse(request).content().getFirst().text();
    }

    public ClaudeMessageResponse getClaudeMessageResponse(ClaudeMessageRequest request) {
        logger.info("Request: {}", request);
        return claudeInterface.getMessageResponse(request);
    }

    public ClaudeMessageResponse getClaudeMessageResponse(String model, String system, String... messages) {
        // Create a list of Message objects from the messages where the first message is a system message
        // and the rest alternate between "user" and "assistant"
        List<Message> alternatingMessages = IntStream.range(0, messages.length)
                .mapToObj(i -> new SimpleMessage(i % 2 == 0 ? "user" : "assistant", messages[i]))
                .collect(Collectors.toList());
        return getClaudeMessageResponse(
                new ClaudeMessageRequest(model, system, 100, 0.5, alternatingMessages));
    }
}
