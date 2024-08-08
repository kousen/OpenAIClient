package com.kousenit.gemini;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

class ExtractorTest {

    @ParameterizedTest(name = "Extract text from {0}")
    @ValueSource(strings = {
            "Austin-Powers---International-Man-of-Mystery.html",
            "Austin-Powers---The-Spy-Who-Shagged-Me.html",
            "Austin-Powers-in-Goldmember-2002.pdf"})
    void austinPowers(String filename) {
        try {
            System.out.println("Extracting text from " + filename);
            String root = "src/main/resources/";
            String extension = filename.substring(filename.lastIndexOf('.') + 1);
            String text = switch (extension) {
                case "html" -> HtmlTextExtractor.extractText("%shtml/%s".formatted(root, filename));
                case "pdf" -> PDFTextExtractor.extractText("%spdfs/%s".formatted(root, filename));
                default -> throw new IllegalArgumentException("Unsupported extension: " + extension);
            };
            assertNotNull(text);
            System.out.println(text.length());
            int wordCount = PDFTextExtractor.countWords(text);
            System.out.printf("Word count: %d%n", wordCount);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}