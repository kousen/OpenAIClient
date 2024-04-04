package com.kousenit.openaiclient.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import com.kousenit.openaiclient.services.OpenAIService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;
import java.util.List;

import static com.kousenit.openaiclient.json.OpenAIRecords.*;
import static com.kousenit.openaiclient.json.OpenAIRecords.Message;
import static com.kousenit.openaiclient.json.OpenAIRecords.SimpleTextContent;
import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class MessageSerializationTest {
    @Autowired
    private JacksonTester<ChatRequest> jacksonTester;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        objectMapper.registerModule(
                new SimpleModule()
                        .addSerializer(SimpleTextContent.class, new SimpleTextContentSerializer())
                        .addSerializer(ComplexContent.class, new ComplexContentSerializer()));
        JacksonTester.initFields(this, objectMapper);
    }

    @Test
    void serializeSimpleTextMessage() throws IOException {
        var request = new ChatRequest(OpenAIService.GPT4, 200, 0.7,
                List.of(new Message(Role.USER,
                        new SimpleTextContent("Hello, World!"))));
        String json = jacksonTester.write(request).getJson();
        System.out.println(json);

        ReadContext ctx = JsonPath.parse(json);
        String contentText = ctx.read("$.messages[0].content");
        assertThat(contentText).isEqualTo("Hello, World!");
    }

    @Test
    void serializeComplexMessage() throws IOException {
        var request = new ChatRequest(OpenAIService.GPT4V, 200, 0.7,
                List.of(new Message(Role.USER,
                        new ComplexContent(
                                List.of(new Text("text", "Hello, World!"),
                                        new ImageUrl("image_url", new Url("https://example.com/image.jpg")))))));
        String json = jacksonTester.write(request).getJson();
        System.out.println(json);

        ReadContext ctx = JsonPath.parse(json);
        String contentText = ctx.read("$.messages[0].content[0].text");
        assertThat(contentText).isEqualTo("Hello, World!");
        String imageUrl = ctx.read("$.messages[0].content[1].image_url.url");
        assertThat(imageUrl).isEqualTo("https://example.com/image.jpg");
    }
}