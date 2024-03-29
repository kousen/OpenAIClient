package com.kousenit.claude.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kousenit.claude.json.ClaudeRequest;
import com.kousenit.claude.json.ClaudeResponse;
import com.kousenit.openaiclient.services.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ClaudeService {
    public static final Logger logger = LoggerFactory.getLogger(ClaudeService.class);
    public static final Double DEFAULT_TEMPERATURE = 0.3;
    public static final Integer MAX_TOKENS_TO_SAMPLE = 1024;

    public final static String CLAUDE_2 = "claude-2";
    public final static String CLAUDE_INSTANT_1 = "claude-instant-1";


    private final ClaudeInterface claudeInterface;
    private final ObjectMapper mapper;

    public ClaudeService(ClaudeInterface claudeInterface, ObjectMapper mapper) {
        this.claudeInterface = claudeInterface;
        this.mapper = mapper;
    }

    // Overloads for prompt, model, and temperature
    public String getClaudeResponse(String prompt) {
        return getClaudeResponse(prompt, CLAUDE_INSTANT_1, DEFAULT_TEMPERATURE);
    }

    public String getClaudeResponse(String prompt, String model) {
        return getClaudeResponse(prompt, model, DEFAULT_TEMPERATURE);
    }

    public String getClaudeResponse(String prompt, double temperature) {
        return getClaudeResponse(prompt, CLAUDE_INSTANT_1, temperature);
    }

    public String getClaudeResponse(String prompt, String model, double temperature) {
        return getClaudeResponse("", prompt, model, temperature);
    }

    public String getClaudeResponse(String system, String prompt, String model, double temperature) {
        ClaudeRequest request = new ClaudeRequest(
                model,
                formatWithSystemPrompt(system, prompt),
                MAX_TOKENS_TO_SAMPLE,
                temperature);
        ClaudeResponse response = claudeInterface.getCompletion(request);
        logger.info(response.toString());
        return response.completion();
    }

    // System prompts provide context and are provided before the first Human: prompt
    private String formatWithSystemPrompt(String system, String prompt) {
        if (system.isEmpty()) {
            return "\n\nHuman: %s\n\nAssistant:".formatted(prompt);
        }

        return "%s\n\nHuman: %s\n\nAssistant:".formatted(system, prompt);
    }

    public Person extractPerson(String prompt) {
        return extractPerson(prompt, CLAUDE_INSTANT_1, DEFAULT_TEMPERATURE);
    }

    public Person extractPerson(String prompt, String model) {
        return extractPerson(prompt, model, DEFAULT_TEMPERATURE);
    }

    public Person extractPerson(String prompt, double temperature) {
        return extractPerson(prompt, CLAUDE_INSTANT_1, temperature);
    }

    public Person extractPerson(String prompt, String model, double temperature) {
        String systemPrompt = """
                Here is a Java record representing a person:
                    record Person(String firstName, String lastName, LocalDate dob) {}
                Please extract the relevant fields from the <person> tags in the next
                message into a JSON representation of a Person object.
                """;
        String text = """
                <person>%s</person>
                """.formatted(prompt);
        try {
            String output = getClaudeResponse(systemPrompt, text, model, temperature);
            logger.debug(output);
            return mapper.readValue(parseJSONFromResponse(output), Person.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private String parseJSONFromResponse(String response) {
        String json = response;
        Pattern pattern = Pattern.compile("```json\n(.*)\n```", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(response);
        if(matcher.find()){
            json = matcher.group(1);
        }
        logger.debug("Extracted: " + json);
        return json;
    }
}
