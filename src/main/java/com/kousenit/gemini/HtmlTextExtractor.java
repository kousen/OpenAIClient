package com.kousenit.gemini;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.html.HtmlParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HtmlTextExtractor {
    public static String extractText(String filePath) throws IOException, TikaException, SAXException {
        Path htmlFilePath = Paths.get(filePath);

        try (InputStream input = Files.newInputStream(htmlFilePath)) {
            // Using HtmlParser specifically
            var parser = new HtmlParser();
            var handler = new BodyContentHandler(-1); // -1 to allow large content
            var metadata = new Metadata();
            var context = new ParseContext();

            // Parse the HTML file
            parser.parse(input, handler, metadata, context);

            return handler.toString();
        }
    }

}
