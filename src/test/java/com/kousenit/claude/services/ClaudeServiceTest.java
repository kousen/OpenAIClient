package com.kousenit.claude.services;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;

import static com.kousenit.claude.json.ClaudeRecords.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ClaudeServiceTest {
    public static final Logger logger = LoggerFactory.getLogger(ClaudeServiceTest.class);

    @Autowired
    private ClaudeService claudeService;

    @Test
    void independentQuestions() {
        var response = claudeService.getClaudeMessageResponse(
                """
                        I'm the person who ran the mutant lab that
                        turned Wade Wilson into DeadPool in the movie.
                        What is my supervillain name?""",
                ClaudeService.CLAUDE_3_HAIKU);
        System.out.println(response);
        response = claudeService.getClaudeMessageResponse("""
                        What did DeadPool call me in the movie?""",
                ClaudeService.CLAUDE_3_HAIKU);
        System.out.println(response);
    }

    @Test
    void conversation() {
        var response = claudeService.getClaudeMessageResponse(
                ClaudeService.CLAUDE_3_HAIKU, """
                        I'm the person who ran the mutant lab that
                        turned Wade Wilson into DeadPool in the movie.
                        """,
                "What is my supervillain name?",
                "Your supervillain name is Ajax.",
                "What's my name?");
        String name = response.content()
                .getFirst()
                .text();
        System.out.println(name);
        assertThat(name).contains("Francis");
    }

    @Test
    void describeImage() throws IOException {
        String imageFileName = "happy_leaping_robot.png";
        String encodedImage = Base64.getEncoder()
                .encodeToString(
                        Files.readAllBytes(Path.of("src/main/resources/images", imageFileName)));
        var request = new ClaudeMessageRequest(
                ClaudeService.CLAUDE_3_HAIKU,
                "",
                1024,
                0.3,
                List.of(new MixedContent("user",
                        List.of(new ImageContent("image",
                                        new ImageSource("base64", "image/png", encodedImage)),
                                new TextContent("text", "What is in this image?")))));
        logger.info("Model: {}", request.model());
        logger.info("maxTokens: {}", request.maxTokens());
        logger.info("temperature: {}", request.temperature());
        List<Message> messages = request.messages();
        MixedContent content = (MixedContent) messages.getFirst();
        ImageContent imageContent = (ImageContent) content.content().getFirst();
        ImageSource source = imageContent.source();
        logger.info("type: {}", source.type());
        logger.info("mimeType: {}", source.mediaType());
        logger.info("image: {}", source.data().substring(0, 20));

        ClaudeMessageResponse response = null;
        try {
            response = claudeService.getClaudeMessageResponse(request);
        } catch (Exception e) {
            logger.error("Error: {}", e.getMessage());
        }
        System.out.println(response);
    }

    @Test
    void pirateCoverLetter_opus() {
        String question = """
                Write a cover letter for a Java developer
                applying for an AI programming position,
                written in pirate speak.
                """;
        var response = claudeService.getClaudeMessageResponse(question, ClaudeService.CLAUDE_3_OPUS);
        System.out.println(response);
        assertThat(response).contains("Ahoy");
    }

    @Test
    void calculatorTest_haiku() {
        String question = """
                Show each step of the calculation.
                What is the square root of the sum of the numbers of letters
                in the words "hello" and "world"?
                """;
        var response = claudeService.getClaudeMessageResponse(
                question, ClaudeService.CLAUDE_3_HAIKU);
        System.out.println(response);
        assertThat(response).contains("3.16");
    }

    @Test
    void haikuTest_haiku() {
        String question = """
                Write a haiku about Java development
                with AI tools.
                Remember that in a haiku, the first line
                should have 5 syllables, the second line 7 syllables,
                and the third line 5 syllables.
                """;
        var response = claudeService.getClaudeMessageResponse(
                question, ClaudeService.CLAUDE_3_HAIKU);
        System.out.println(response);
        assertThat(response).isNotBlank();
    }

    @Test
    void calculatorTest_sonnet() {
        String question = """
                Show each step of the calculation.
                What is the square root of the sum of the numbers of letters
                in the words "hello" and "world"?
                """;
        var response = claudeService.getClaudeMessageResponse(
                question, ClaudeService.CLAUDE_3_SONNET);
        System.out.println(response);
        assertThat(response).contains("3.16");
    }

    @Test
    void calculatorTest_opus() {
        String question = """
                Show each step of the calculation.
                What is the square root of the sum of the numbers of letters
                in the words "hello" and "world"?
                """;
        var response = claudeService.getClaudeMessageResponse(question, ClaudeService.CLAUDE_3_OPUS);
        System.out.println(response);
        assertThat(response).contains("3.16");
    }
}