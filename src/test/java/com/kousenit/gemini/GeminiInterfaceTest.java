package com.kousenit.gemini;

import org.apache.tika.exception.TikaException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static com.kousenit.gemini.GeminiRecords.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class GeminiInterfaceTest {
    @Autowired
    private GeminiInterface geminiInterface;

    private String readBook(String filename) throws IOException, TikaException, SAXException {
        String extension = filename.substring(filename.lastIndexOf('.'));
        return switch (extension) {
            case ".pdf" -> PDFTextExtractor.extractText("src/main/resources/pdfs/" + filename);
            case ".html" -> HtmlTextExtractor.extractText("src/main/resources/html/" + filename);
            case ".txt" -> Files.readString(Path.of("src/main/resources/text/books/" + filename));
            default -> throw new IllegalArgumentException("Unknown file extension: " + extension);
        };
    }

    @ParameterizedTest(name = "Count tokens in {0}")
    @ValueSource(strings = {
            "Austin-Powers---International-Man-of-Mystery.html",
            "Austin-Powers---The-Spy-Who-Shagged-Me.html",
            "Austin-Powers-in-Goldmember-2002.pdf"})
    void countTokens(String filename) throws Exception {
        var content = new Content(
                List.of(new TextPart(readBook(filename))),
                "user");
        var request = new CountTokensRequest(
                List.of(content),
                new GenerateContentRequest(
                        "models/" + GeminiService.GEMINI_1_5_FLASH,
                        List.of(content),
                        List.of(),
                        null, null, null,
                        null, null));
        GeminiCountResponse response = geminiInterface.countTokens(GeminiService.GEMINI_1_5_FLASH, request);
        assertNotNull(response);
        System.out.println(response);
    }

    // CachedContent[contents=null, tools=null, createTime=2024-06-23T15:23:42.406048Z,
    // updateTime=2024-06-23T15:23:42.406048Z, usageMetadata=UsageMetadata[totalTokenCount=79447],
    // ttl=null, name=cachedContents/m4qhg6ir3lqq, displayName=, model=models/gemini-1.5-flash-001,
    // systemInstruction=null, toolConfig=null]
    @Test
    void createCachedContents() throws TikaException, IOException, SAXException {
        CachedContent cachedContents = geminiInterface.createCachedContents(
                new CachedContent(
                        List.of(new Content(List.of(new TextPart(
                                readBook("help-your-boss-help-you_P1.0"))),
                                "user")),
                        null,
                        null,
                        null,
                        null,
                        "600s",
                        null,
                        "HYBHY",
                        "models/gemini-1.5-flash-001",
                        null,
                        null));
        assertNotNull(cachedContents);
        System.out.println(cachedContents);

        // Questions using the cache:
        List<String> questions = List.of(
                "How many chapters are in the book?",
                "What is the main theme of the book?",
                "Please summarize the three most important points."
        );

        questions.forEach(question ->
                System.out.println(geminiInterface.getCompletion("gemini-1.5-flash-001",
                        new GeminiRequest(
                                List.of(new Content(List.of(new TextPart(question)), "user")),
                                cachedContents.name()))));
    }

    @Test
    void listCachedContents() {
        CachedContentResponse cachedContentResponse = geminiInterface.listCachedContents();
        assertNotNull(cachedContentResponse);
        cachedContentResponse.cachedContents()
                .forEach(cachedContent -> System.out.println(cachedContent.name()));
    }

    @Test
    void getCachedContent() {
        CachedContentResponse cachedContentResponse = geminiInterface.listCachedContents();
        String name = cachedContentResponse.cachedContents()
                .getFirst()
                .name();
        CachedContent cachedContent = geminiInterface.getCachedContent(name);
        assertNotNull(cachedContent);
        System.out.println(cachedContent);
    }

    @Test
    void deleteCachedContent() {
    }
}