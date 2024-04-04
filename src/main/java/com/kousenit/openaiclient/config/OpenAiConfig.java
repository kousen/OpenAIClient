package com.kousenit.openaiclient.config;

import com.kousenit.openaiclient.json.ComplexContentSerializer;
import com.kousenit.openaiclient.json.SimpleTextContentSerializer;
import com.kousenit.openaiclient.services.OpenAIInterface;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Configuration
public class OpenAiConfig {
    @Bean
    public WebClient openAIWebClient(@Value("${openai.baseurl}") String baseUrl,
                                     @Value("${OPENAI_API_KEY}") String apiKey,
                                     @Value("${whisper.max_allowed_size_bytes}") DataSize maxAllowedSize) {
        return WebClient.builder()
                .defaultHeader("Authorization", "Bearer %s".formatted(apiKey))
                .filter(logRequest())
                .filter(logResponse())
                .codecs(configurer -> configurer.defaultCodecs()
                        .maxInMemorySize((int) maxAllowedSize.toBytes()))
                .baseUrl(baseUrl)
                .build();
    }

    private ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            // Log request headers and body here
            System.out.println("Request: " + clientRequest.url());
            clientRequest.headers().forEach((name, values) ->
                    values.forEach(value -> System.out.println(name + ":" + value)));
            return Mono.just(clientRequest);
        });
    }

    private ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            // Log response headers and body here
            System.out.println("Response Status Code: " + clientResponse.statusCode());
            clientResponse.headers().asHttpHeaders().forEach((name, values) ->
                    values.forEach(value -> System.out.println(name + ":" + value)));
            return Mono.just(clientResponse);
        });
    }

    @Bean
    public OpenAIInterface openAIInterface(@Qualifier("openAIWebClient") WebClient client) {
        WebClientAdapter adapter = WebClientAdapter.create(client);
        adapter.setBlockTimeout(Duration.of(2, ChronoUnit.MINUTES));
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(OpenAIInterface.class);
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
        return builder -> builder.serializers(
                new SimpleTextContentSerializer(),
                new ComplexContentSerializer());
    }

}
