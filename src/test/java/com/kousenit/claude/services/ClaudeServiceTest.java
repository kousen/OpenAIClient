package com.kousenit.claude.services;

import com.kousenit.ollamaclient.utils.FileUtils;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static com.kousenit.claude.json.ClaudeRecords.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ClaudeServiceTest {
    public static final Logger logger =
            LoggerFactory.getLogger(ClaudeServiceTest.class);

    @Autowired
    private ClaudeService claudeService;

    @Nested
    class CompositionTests {
        @Test
        void haikuTest_haiku() {
            String question = """
                    Write a haiku about Java development
                    with AI tools.
                    """;
            var response = claudeService.getClaudeMessageResponse(
                    question, ClaudeService.CLAUDE_3_HAIKU);
            System.out.println(response);
            assertThat(response).isNotBlank();
        }

        @Test
        void sonnetTest_sonnet() {
            String question = """
                    Write a sonnet about Java development
                    with AI tools.
                    """;
            var response = claudeService.getClaudeMessageResponse(
                    question, ClaudeService.CLAUDE_35_SONNET);
            System.out.println(response);
            assertThat(response).isNotBlank();
        }

        @Test
        void opusTest_opus() {
            String question = """
                    Write an opus about Java development
                    with AI tools.
                    """;
            var response = claudeService.getClaudeMessageResponse(
                    question, ClaudeService.CLAUDE_3_OPUS);
            System.out.println(response);
            assertThat(response).isNotBlank();
        }
    }

    @Nested
    class ConversationTests {
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
    }

    @Nested
    class BasicTests {

        @Test
        void hello_world_in_klingon() {
            ClaudeMessageResponse response = claudeService.getClaudeMessageResponse(
                    new ClaudeMessageRequest(
                            ClaudeService.CLAUDE_3_HAIKU,
                            "",
                            1024,
                            0.3,
                            List.of(new SimpleMessage("user", "Hello, Dolly"))
                    ));
            System.out.println(response);
        }

        @Test
        void simpleMessage() {
            var request = new ClaudeMessageRequest(
                    ClaudeService.CLAUDE_3_HAIKU,
                    "",
                    1024,
                    0.3,
                    List.of(new SimpleMessage("user", "What is the capital of France?")));
            var response = claudeService.getClaudeMessageResponse(request);
            System.out.println(response);
            assertThat(response.content().getFirst().text()).contains("Paris");
        }

        @Test
        void textMessage() {
            var request = new ClaudeMessageRequest(
                    ClaudeService.CLAUDE_3_HAIKU,
                    "",
                    1024,
                    0.3,
                    List.of(new TextMessage("user",
                            List.of(new TextContent("text", "What is the capital of France?")))));
            var response = claudeService.getClaudeMessageResponse(request);
            System.out.println(response);
            assertThat(response.content().getFirst().text()).contains("Paris");
        }

        @Test
        void mixedContentMessage() {
            String imageFileName = "happy_leaping_robot.png";
            String encodedImage = FileUtils.encodeImage(
                    "src/main/resources/images/%s".formatted(imageFileName));
            var request = new ClaudeMessageRequest(
                    ClaudeService.CLAUDE_3_HAIKU,
                    "",
                    1024,
                    0.3,
                    List.of(new MixedContent("user",
                            List.of(new ImageContent("image",
                                            new ImageContent.ImageSource(
                                                    "base64", "image/png", encodedImage)),
                                    new TextContent("text", "What is in this image?")))));

            ClaudeMessageResponse response = null;
            try {
                response = claudeService.getClaudeMessageResponse(request);
            } catch (Exception e) {
                logger.error("Error: {}", e.getMessage());
            }
            System.out.println(response);
        }
    }

    @Test
    void pirateCoverLetter_haiku() {
        String question = """
                Write a cover letter for a Java developer
                applying for an AI programming position,
                written in pirate speak.
                """;
        var response = claudeService.getClaudeMessageResponse(question, ClaudeService.CLAUDE_3_HAIKU);
        System.out.println(response);
        assertThat(response).contains("Ahoy");
    }

    @Nested
    class CalculatorTests {
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
}