package com.kousenit.gemini;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.ocr.TesseractOCRConfig;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class PDFTextExtractor {
    public static int countWords(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }
        // Split the string based on spaces and punctuation.
        String[] words = text.split("\\s+|,|\\.|\\(|\\)|\\[|]|!|\\?|;|:");
        return words.length;
    }

    public static String extractText(String pdfFilePath) throws IOException, TikaException, SAXException {
        var pdfParser = new PDFParser();

        // Remove the limit on file size
        var handler = new BodyContentHandler(-1);
        var metadata = new Metadata();

        try (InputStream inputstream = new FileInputStream(pdfFilePath)) {
            var context = new ParseContext();
            var config = new TesseractOCRConfig();
            config.setSkipOcr(true);
            context.set(TesseractOCRConfig.class, config);

            // Parse the PDF file
            pdfParser.parse(inputstream, handler, metadata, context);
        }
        return handler.toString();
    }
}
