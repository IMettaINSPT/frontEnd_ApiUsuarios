package com.tp.frontend.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class HttpClientConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder, FrontendProperties props) {
        return builder
                .setConnectTimeout(Duration.ofMillis(props.getConnectTimeoutMs()))
                .setReadTimeout(Duration.ofMillis(props.getReadTimeoutMs()))
                .build();
    }
}
