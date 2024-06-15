package com.kousenit.ollamaclient.config;

import com.kousenit.ollamaclient.services.OllamaInterface;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class OllamaConfig {
    @Bean
    public RestClient ollamaRestClient(@Value("${ollama.baseurl}") String baseUrl) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Accept", "application/json")
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    @Bean
    public OllamaInterface ollamaInterface(@Qualifier("ollamaRestClient") RestClient client) {
        RestClientAdapter adapter = RestClientAdapter.create(client);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter)
                .build();
        return factory.createClient(OllamaInterface.class);
    }

    @Bean
    public WebClient ollamaWebClient(@Value("${ollama.baseurl}") String baseUrl) {
//        HttpClient httpClient = HttpClient.create(ConnectionProvider.newConnection())
//                .responseTimeout(Duration.ofMinutes(2)) // Adjust the timeout as necessary
//                .keepAlive(true)
//                .wiretap(true);
//
//        var strategies = ExchangeStrategies.builder()
//                .codecs(configurer -> configurer.defaultCodecs()
//                        .jackson2JsonDecoder(
//                                new Jackson2JsonDecoder(
//                                        new ObjectMapper(), MediaType.APPLICATION_NDJSON)))
//                .build();

        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Accept", MediaType.APPLICATION_NDJSON_VALUE)
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                // .clientConnector(new ReactorClientHttpConnector(httpClient))
                // .exchangeStrategies(strategies)
                .build();
    }

    @Bean
    public OllamaInterface ollamaAsyncInterface(@Qualifier("ollamaWebClient") WebClient client) {
        WebClientAdapter adapter = WebClientAdapter.create(client);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(OllamaInterface.class);
    }
}
