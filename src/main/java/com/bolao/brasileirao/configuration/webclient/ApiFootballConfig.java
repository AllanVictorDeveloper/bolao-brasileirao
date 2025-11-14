package com.bolao.brasileirao.configuration.webclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ApiFootballConfig {

    @Value("${api.football.key}")
    private String apiKey;

    @Value("${api.football.base-url}")
    private String baseUrl;

    @Bean
    public WebClient apiFutebolClient() {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
    }

}
