package com.bolao.brasileirao.configuration.webclient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;

@Configuration
public class ApiFootballConfig {

    @Value("${api.football.key}")
    private String apiKey;

    @Value("${api.football.base-url}")
    private String baseUrl;

    @Bean
    public RestTemplate apiFutebolClient() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setInterceptors(List.of(new AuthorizationInterceptor("Bearer " + apiKey)));
        return restTemplate;
    }

    @Bean
    public String apiFutebolBaseUrl() {
        return baseUrl;
    }

    private record AuthorizationInterceptor(String token) implements ClientHttpRequestInterceptor {
        @Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                                            ClientHttpRequestExecution execution) throws IOException {
            request.getHeaders().set("Authorization", token);
            return execution.execute(request, body);
        }
    }
}
