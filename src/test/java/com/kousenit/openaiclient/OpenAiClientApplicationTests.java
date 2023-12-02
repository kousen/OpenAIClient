package com.kousenit.openaiclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kousenit.openaiclient.services.Person;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class OpenAiClientApplicationTests {

    @Test
    void contextLoads(@Autowired ObjectMapper mapper) throws JsonProcessingException {
        String json = """
                {
                    "firstName": "John",
                    "lastName": "Bigbooté",
                    "dob": "1938-11-01"
                }
                """;
        Person person = mapper.readValue(json, Person.class);
        System.out.println(person);
        System.out.println(person.dob().getYear());
    }

}
