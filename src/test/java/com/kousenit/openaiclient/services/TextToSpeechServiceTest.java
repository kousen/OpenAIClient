package com.kousenit.openaiclient.services;

import com.kousenit.openaiclient.json.Voice;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TextToSpeechServiceTest {
    @Autowired
    private TextToSpeechService service;

    private final String audioPrompt = """
            The YouTube channel, "Tales from the jar side" is your best
            source for learning about Java, Spring, and other open source
            technologies, especially when combined with AI tools.
            The companion newsletter of the same name is also a lot of fun.
            """.replaceAll("\\s+", " ").trim();

    @Test
    void getAudioResponse() {
        byte[] audioResponse = service.getAudioResponse(audioPrompt);
        assertThat(audioResponse.length).isPositive();
    }

    @Test
    void getAudioResponseWithVoice() {
        byte[] audioResponse = service.getAudioResponse(audioPrompt, Voice.SHIMMER);
        assertThat(audioResponse.length).isPositive();
    }

    @Test
    void getAudioResponseWithModelAndVoice() {
        byte[] audioResponse = service.getAudioResponse(
                TextToSpeechService.TTS_1_HD, audioPrompt, Voice.FABLE);
        assertThat(audioResponse.length).isPositive();
    }

    @Test
    void playMp3UsingJLayer() {
        service.playMp3UsingJLayer("tftjs.mp3");
    }

    @Test
    void createAndPlay() {
        Voice voice = Voice.randomVoice();
        System.out.println("Using voice " + voice);
        service.createAndPlay(audioPrompt, voice);
    }

    @Test
    // NOTE: The JUnit library is referred to here as J-Unit to make
    // the audio come out correctly.
    void createAndPlay_includingTechnicalWords() {
        Voice voice = Voice.randomVoice();
        System.out.println("Using voice " + voice);
        service.createAndPlay("""
                This application uses Spring Boot's HTTP exchange interfaces,
                as well as Java records, text blocks, enums, and the var
                reserved type name, to access the OpenAI API web service.
                
                The embedded JSON parser is Jackson, customized to use
                Java records instead of POJOs, and the enums are translated
                to lowercase using the @JsonValue annotation.
                
                Testing is done with J-Unit 5 and the AssertJ testing library.
                
                Since the cost of the base TTS model is only 1.5 cents per
                1000 characters, this test cost less than a penny to run.
                """, voice);
    }

    @Test
    void createAndPlay_withVoice() {
        service.createAndPlay("""
                 Here are 5 key bullet points summarizing the video:
                 * Demonstrates using Spring Boot and OpenAI's text-to-speech API
                  to convert text into audio MP3 files
                 * Maps the OpenAI API request/response format into a Java record
                  class for easy access
                 * Implements a Spring HTTP exchange interface to call the API with
                  proper annotations
                 * Saves the returned audio byte array into an MP3 file for playback
                 * Shows how to add request validation and bundle it all into a
                  fast-starting GraalVM native image executable
                """, Voice.FABLE);
    }
}