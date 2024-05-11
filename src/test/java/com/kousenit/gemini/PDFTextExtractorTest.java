package com.kousenit.gemini;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PDFTextExtractorTest {

    @Test
    void extractText() {
        try {
            String filePath = "src/main/resources/pdfs/help-your-boss-help-you_P1.0.pdf";
            String text = PDFTextExtractor.extractText(filePath);
            assertNotNull(text);
            System.out.println(text.length());
            int wordCount = PDFTextExtractor.countWords(text);
            System.out.printf("Word count: %d%n", wordCount);
            assertTrue(wordCount > 63000);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}