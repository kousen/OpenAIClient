package com.kousenit.gemini;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.kousenit.gemini.GeminiRecords.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class GeminiServiceTest {
    @Autowired
    private GeminiService service;

    @Test
    void getCompletion_HHGtTG_question() {
        String text = service.getCompletion("""
            What is the Ultimate Answer to
            the Ultimate Question of Life, the Universe,
            and Everything?
            """);
        assertNotNull(text);
        System.out.println(text);
        assertThat(text).contains("42");
    }

    @Test
    void getCompletion() {
        String text = service.getCompletion("""
            How many roads must a man walk down
            before you can call him a man?
            """);
        assertNotNull(text);
        System.out.println(text);
    }

    @Test
    void pirateCoverLetter() {
        String text = service.getCompletion("""
            Please write a cover letter for a Java developer
            applying for an AI position, written in pirate speak.
            """);
        assertNotNull(text);
        System.out.println(text);
    }


    @Test
    void writeAStory() {
        String text = service.getCompletion("Write a story about a magic backpack.");
        assertNotNull(text);
        System.out.println(text);
    }

    @Test
    void describeAnImage() throws Exception {
        String text = service.getCompletionWithImage(
                "Describe this image",
                "A_cheerful_robot.png");
        assertNotNull(text);
        System.out.println(text);
    }

    @Test
    void countItems_gemini_1_5() throws Exception {
        String text = service.analyzeImage(
                """
                This is a picture from the food pantry. It contains
                shelves labeled "canned goods", "snacks", and
                "menstrual care". On the shelf labeled "canned goods",
                how many cans of food are there?
                """,
                "foodnstuff_picture.png");
        assertNotNull(text);
        System.out.println(text);
    }

    @Test
    void getModels() {
        ModelList models = service.getModels();
        assertNotNull(models);
        models.models().stream()
                .map(Model::name)
                .sorted()
                .forEach(System.out::println);
    }

    @Test
    void getCompletionWith15Flash() throws Exception {
        String hybhy = PDFTextExtractor.extractText(
                "src/main/resources/pdfs/help-your-boss-help-you_P1.0.pdf");

        String prompt = """
            Here is the text from the book "Help Your Boss Help You":
            
            %s
            
            Answer the following question based on information
            contained in the book:
            
            %s
            """.formatted(hybhy, "What are the top five major points made in the book?");

        GeminiResponse response = service.getCompletionWithModel(
                GeminiService.GEMINI_1_5_FLASH,
                new GeminiRequest(List.of(new Content(List.of(new TextPart(prompt)), "user")),
                        null));
        System.out.println(response);
        String text = response.candidates().getFirst().content().parts().getFirst().text();
        assertNotNull(text);
        System.out.println(text);
        System.out.println("Input Tokens : " + service.countTokens(prompt));
        System.out.println("Output Tokens: " + service.countTokens(text));
    }


    @Test
    void getCompletionWith15Pro() throws Exception {
        String hybhy = PDFTextExtractor.extractText(
                "src/main/resources/pdfs/help-your-boss-help-you_P1.0.pdf");

        String prompt = """
            Here is the text from the book "Help Your Boss Help You":
            
            %s
            
            Answer the following question based on information
            contained in the book:
            
            %s
            """.formatted(hybhy, "What are the top five major points made in the book?");

        GeminiResponse response = service.getCompletionWithModel(
                GeminiService.GEMINI_1_5_PRO,
                new GeminiRequest(List.of(new Content(List.of(new TextPart(prompt)), "user")),
                        null));
        System.out.println(response);
        String text = response.candidates().getFirst().content().parts().getFirst().text();
        assertNotNull(text);
        System.out.println(text);
        System.out.println("Input Tokens : " + service.countTokens(prompt));
        System.out.println("Output Tokens: " + service.countTokens(text));
    }

    @Test
    void countTokens_fullRequest() {
        var content = new Content(
                List.of(new TextPart("What is the airspeed velocity of an unladen swallow?")), "user");
        var request = new CountTokensRequest(
                List.of(content),
                new GenerateContentRequest("models/gemini-1.5-flash-001",
                        List.of(content), List.of(), null, null, null,
                        null, null));
        GeminiCountResponse response = service.countTokens(GeminiService.GEMINI_PRO, request);
        assertNotNull(response);
        System.out.println(response);
        assertThat(response.totalTokens()).isEqualTo(13);
    }

    @ParameterizedTest(name = "tokens({0})")
    @CsvFileSource(resources = "/books.csv", numLinesToSkip = 1)
    void countBookTokens(String fileName) throws IOException {
        // Load the file from the classpath
        try (InputStream inputStream =
                     getClass().getClassLoader().getResourceAsStream("text/books/" + fileName)) {
            System.out.println("Reading " + fileName);
            if (inputStream == null) {
                throw new IOException("Could not read file " + fileName);
            }
            String book = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            int totalTokens = service.countTokens(book);
            System.out.println(totalTokens);
        }
    }

    @ParameterizedTest(name = "summarize({0})")
    @CsvFileSource(resources = "/books.csv", numLinesToSkip = 1)
    void summarizePlot(String fileName, int tokens) throws IOException {
        if (tokens > 1_500_000) {
            System.out.printf("Skipping %s with %d tokens%n", fileName, tokens);
            return;
        }
        try (InputStream inputStream =
                     getClass().getClassLoader()
                             .getResourceAsStream("text/books/" + fileName)) {
            if (inputStream == null) {
                throw new IOException("Could not read file " + fileName);
            }
            String book = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            System.out.println(book.length() + " characters");
            String prompt = """
                    Summarize the plot of the book:
                    
                    %s
                    
                    Explain why it might wind up on a
                    Banned Books list.
                    """.formatted(book);
            GeminiResponse response = service.getCompletionWithModel(
                    GeminiService.GEMINI_1_5_PRO,
                    new GeminiRequest(List.of(new Content(List.of(new TextPart(prompt)), "user")),
                            null));
            System.out.println(response);
        }
    }


    @Test
    void countBookTokensHYBHY() throws Exception {
        String hybhy = PDFTextExtractor.extractText(
                "src/main/resources/pdfs/help-your-boss-help-you_P1.0.pdf");
        int totalTokens = service.countTokens(hybhy);
        System.out.println(totalTokens);
    }

}