package com.kousenit.openaiclient.util;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import com.kousenit.openaiclient.json.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;

import static com.kousenit.openaiclient.json.OpenAIRecords.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JsonTest
class SerializeRoleTest {
    @Autowired
    private JacksonTester<Message> jacksonTester;

    @ParameterizedTest(name = "Serializing {index} => {0}")
    @EnumSource(Role.class)
    void serializeRole(Role role) throws IOException {
        Message message = new Message(role, "Hello, world!");
        String json = jacksonTester.write(message).getJson();
        System.out.println(json);
        assertThat(json).contains(role.name().toLowerCase());

        ReadContext ctx = JsonPath.parse(json);
        String roleString = ctx.read("$.role");
        assertThat(role.name().toLowerCase()).isEqualTo(roleString);
    }

    @Test
    void deserializeRole() throws IOException {
        String json = """
                {
                    "role" : "user",
                    "content" : "Hello, world!"
                }""";
        Message message = jacksonTester.parseObject(json);
        assertAll(
                () -> assertNotNull(message),
                () -> assertEquals(Role.USER, message.role()),
                () -> assertEquals("Hello, world!", message.content())
        );
    }

    @Test
    void testRecordSerialization() throws IOException {
        Message message = new Message(Role.USER, "Hello, world!");
        String serializedRequest = jacksonTester.write(message).getJson();
        System.out.println(serializedRequest);
        Message deserializedRequest = jacksonTester.parseObject(serializedRequest);
        assertEquals(message, deserializedRequest);
    }
}