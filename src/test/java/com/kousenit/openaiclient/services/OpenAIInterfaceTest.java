package com.kousenit.openaiclient.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.kousenit.openaiclient.json.OpenAIRecords.ModelList;

@SpringBootTest
class OpenAIInterfaceTest {

    @Autowired
    private OpenAIInterface openAIInterface;

    @Test
    void listModelIds() {
        openAIInterface.listModels().data().stream()
                .map(ModelList.Model::id)
                .sorted()
                .forEach(System.out::println);
    }

    @Test
    void listGPTModels() {
        openAIInterface.listModels().data().stream()
                .filter(model -> model.id().contains("gpt"))
                .forEach(System.out::println);
    }

}