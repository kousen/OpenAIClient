package com.kousenit.openaiclient.config;

import com.kousenit.openaiclient.services.OpenAIInterface;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Configuration
public class OpenAiConfig {

    @Bean
    public WebClient openAIWebClient(
            @Value("${openai.baseurl}") String baseUrl,
            @Value("${OPENAI_API_KEY}") String apiKey,
            @Value("${whisper.max_allowed_size}") DataSize maxAllowedSize) {

        return WebClient.builder()
                .defaultHeader("Authorization", "Bearer %s".formatted(apiKey))
                .codecs(configurer -> configurer.defaultCodecs()
                        .maxInMemorySize((int) maxAllowedSize.toBytes()))
                .baseUrl(baseUrl)
                .build();
    }

    @Bean
    public RestClient openAIRestClient(
            @Value("${openai.baseurl}") String baseUrl,
            @Value("${OPENAI_API_KEY}") String apiKey) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "Bearer %s".formatted(apiKey))
                .defaultHeader("Accept", "application/json")
                .defaultHeader("Content-Type", "multipart/form-data")
                .build();
    }

    @Bean
    public OpenAIInterface openAIInterface(@Qualifier("openAIWebClient") WebClient client) {
        WebClientAdapter adapter = WebClientAdapter.create(client);
        adapter.setBlockTimeout(Duration.of(2, ChronoUnit.MINUTES));
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(OpenAIInterface.class);
    }
}
