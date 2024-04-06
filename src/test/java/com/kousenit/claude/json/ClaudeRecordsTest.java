package com.kousenit.claude.json;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static com.kousenit.claude.json.ClaudeRecords.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ClaudeRecordsTest {

    @Test
    void testSimpleMessageCreation() {
        var message = new SimpleMessage("user", "Hello, Claude");
        assertEquals("user", message.role());
        assertEquals("Hello, Claude", message.content());
    }

    @Test
    void testTextMessageCreation() {
        TextContent content = new TextContent("text", "Hello, Claude");
        TextMessage message = new TextMessage("user", List.of(content));

        assertAll(() -> {
            assertEquals("user", message.role());
            assertFalse(message.content().isEmpty());
            assertEquals("text", message.content().getFirst().type());
            assertEquals("Hello, Claude", message.content().getFirst().text());
        });
    }

    @Test
    void testMixedContentMessageCreation() {
        var imageSource = new ImageContent.ImageSource("base64", "image/jpeg", "imageData");
        Content imageContent = new ImageContent("image", imageSource);
        Content textContent = new TextContent("text", "What is in this image?");
        MixedContent message = new MixedContent("user", List.of(imageContent, textContent));

        assertAll(() -> {
            assertEquals("user", message.role());
            assertFalse(message.content().isEmpty());
            assertInstanceOf(ImageContent.class, message.content().get(0));
            assertInstanceOf(TextContent.class, message.content().get(1));
        });
    }
}
