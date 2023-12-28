package com.kousenit.openaiclient.config;

import com.kousenit.openaiclient.services.ClaudeInterface;
import com.kousenit.openaiclient.services.OpenAIInterface;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Configuration
public class AppConfig {
    @Bean
    public WebClient openAIWebClient(@Value("${openai.baseurl}") String baseUrl,
                                     @Value("${OPENAI_API_KEY}") String apiKey,
                                     @Value("${whisper.max_allowed_size_bytes}") DataSize maxAllowedSize) {
        return WebClient.builder()
                .defaultHeader("Authorization", "Bearer %s".formatted(apiKey))
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize((int) maxAllowedSize.toBytes()))
                .baseUrl(baseUrl)
                .build();
    }

    @Bean
    public OpenAIInterface openAIInterface(@Qualifier("openAIWebClient") WebClient client) {
        WebClientAdapter adapter = WebClientAdapter.create(client);
        adapter.setBlockTimeout(Duration.of(2, ChronoUnit.MINUTES));
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(OpenAIInterface.class);
    }

    @Bean
    public RestClient claudeRestClient(@Value("${claude.baseurl}") String baseUrl,
                                       @Value("${anthropic.api.key}") String apiKey) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Accept", "application/json")
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("anthropic-version", "2023-06-01")
                .defaultHeader("x-api-key", apiKey)
                .build();
    }

    @Bean
    public ClaudeInterface claudeInterface(RestClient client) {
        RestClientAdapter adapter = RestClientAdapter.create(client);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(ClaudeInterface.class);
    }

}
