package com.kousenit.openaiclient.config;

import com.kousenit.openaiclient.services.OpenAIInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Configuration
public class AppConfig {
    private final Logger log = LoggerFactory.getLogger(AppConfig.class);

    @Bean
    public WebClient createWebClient(@Value("${openai.baseurl}") String baseUrl,
                                     @Value("${OPENAI_API_KEY}") String apiKey,
                                     @Value("${whisper.max_allowed_size_bytes}") DataSize maxAllowedSize) {
        return WebClient.builder()
                .filter(this::logRequest)
                .defaultHeader("Authorization", "Bearer %s".formatted(apiKey))
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize((int) maxAllowedSize.toBytes()))
                .baseUrl(baseUrl)
                .build();
    }

    // Only log the headers in debug mode (one of the headers is the OPENAI_API_KEY)
    private Mono<ClientResponse> logRequest(ClientRequest clientRequest, ExchangeFunction next) {
        log.info("Request: {} {}", clientRequest.method(), clientRequest.url());
        clientRequest.headers().forEach((name, values) ->
                values.forEach(value -> log.debug("{}={}", name, value)));
        return next.exchange(clientRequest);
    }

    @Bean
    public OpenAIInterface openAIInterface(WebClient client) {
        HttpServiceProxyFactory factory =
                HttpServiceProxyFactory.builder(WebClientAdapter.forClient(client))
                        .blockTimeout(Duration.of(2, ChronoUnit.MINUTES))
                        .build();
        return factory.createClient(OpenAIInterface.class);
    }

}
