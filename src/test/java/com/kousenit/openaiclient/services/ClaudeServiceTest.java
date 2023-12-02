package com.kousenit.openaiclient.services;

import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

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

    @Test
    void extractPerson() {
        int yearsFromNow = 2305 - LocalDate.now().getYear();
        var person = claudeService.extractPerson("""
                Captain Picard was born on the 13th of juillet, %d years from now,
                in La Barre, France, Earth. His given name, Jean-Luc, is of French
                origin and translates to "John Luke".
                """.formatted(yearsFromNow));
        System.out.println(person);
        assertAll(
                () -> assertThat(person.firstName()).isEqualTo("Jean-Luc"),
                () -> assertThat(person.lastName()).isEqualTo("Picard"),
                () -> assertThat(person.dob().getYear()).isCloseTo(2305, Offset.offset(1))
        );
    }

}