package com.ahmrh.githubactivity.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    RestClient githubRestClient(RestClient.Builder builder) {
        return builder
                .baseUrl("https://api.github.com")
                .defaultHeader("Accept", "application/vnd.github+json")
                .build();
    }
}
