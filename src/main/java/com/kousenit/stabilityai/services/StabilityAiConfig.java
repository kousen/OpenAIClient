package com.kousenit.stabilityai.services;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class StabilityAiConfig {

    @Bean
    public RestClient stabilityAiRestClient(@Value("${stabilityai.baseurl}") String baseUrl,
                                       @Value("${STABILITY_API_KEY}") String apiKey) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "Bearer %s".formatted(apiKey))
                .build();
    }

    @Bean
    public StabilityAiInterface stabilityAiInterface(@Qualifier("stabilityAiRestClient") RestClient client) {
        RestClientAdapter adapter = RestClientAdapter.create(client);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(StabilityAiInterface.class);
    }
}
