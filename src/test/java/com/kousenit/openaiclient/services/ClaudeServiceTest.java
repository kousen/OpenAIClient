package com.kousenit.openaiclient.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ClaudeServiceTest {
    @Autowired
    private ClaudeService claudeService;

    @Test
    void getClaudeResponseToHHG2tG() {
        var response = claudeService.getClaudeResponse(
                """                               
                        According to Douglas Adams, what is the Ultimate Answer
                        to the Ultimate Question of Life, the Universe, and Everything?
                        """
        );
        System.out.println(response);
        assertThat(response).contains("42");
    }
}