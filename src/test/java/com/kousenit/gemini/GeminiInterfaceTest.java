package com.kousenit.gemini;

import org.apache.tika.exception.TikaException;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.List;

import static com.kousenit.gemini.GeminiRecords.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Tag("current")
class GeminiInterfaceTest {
    @Autowired
    private GeminiInterface geminiInterface;

    private String readBook() throws IOException, TikaException, SAXException {
        return PDFTextExtractor.extractText(
                "src/main/resources/pdfs/help-your-boss-help-you_P1.0.pdf");
    }

    // CachedContent[contents=null, tools=null, createTime=2024-06-23T15:23:42.406048Z,
    // updateTime=2024-06-23T15:23:42.406048Z, usageMetadata=UsageMetadata[totalTokenCount=79447],
    // ttl=null, name=cachedContents/m4qhg6ir3lqq, displayName=, model=models/gemini-1.5-flash-001,
    // systemInstruction=null, toolConfig=null]
    @Test
    void createCachedContents() throws TikaException, IOException, SAXException {
        CachedContent cachedContents = geminiInterface.createCachedContents(
                new CachedContent(
                        List.of(new Content(List.of(new TextPart(readBook())), "user")),
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
        CachedContent cachedContent = geminiInterface.getCachedContent("m4qhg6ir3lqq");
        assertNotNull(cachedContent);
        System.out.println(cachedContent);
    }

    @Test
    void deleteCachedContent() {
    }
}