package com.kousenit.stabilityai.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.kousenit.stabilityai.json.StabilityAiRecords;
import com.kousenit.stabilityai.json.VideoResponseDeserializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
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
                //.requestInterceptor(new LoggingInterceptor())
                .defaultHeader("Authorization", "Bearer %s".formatted(apiKey))
                .build();
    }

    @Bean
    public StabilityAiInterface stabilityAiInterface(@Qualifier("stabilityAiRestClient") RestClient client) {
        RestClientAdapter adapter = RestClientAdapter.create(client);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(StabilityAiInterface.class);
    }

    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(10);
        scheduler.setThreadNamePrefix("stabilityai-");
        return scheduler;
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer addCustomDeserializers() {
        return jacksonObjectMapperBuilder -> {
            SimpleModule module = new SimpleModule();
            module.addDeserializer(StabilityAiRecords.VideoResponse.class, new VideoResponseDeserializer());
            jacksonObjectMapperBuilder.modules(module);
        };
    }

    @Bean
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        return builder.createXmlMapper(false).build();
    }
}
