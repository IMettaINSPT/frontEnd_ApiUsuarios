package com.tp.frontend.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
public class HttpClientConfig {

    @Bean
    public WebClient webClient(FrontendProperties props) {

        // Normaliza baseUrl (evita doble "//" al concatenar)
        String baseUrl = normalizeBaseUrl(props.getBackendBaseUrl());

        // Reactor Netty HTTP client con timeouts
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, props.getConnectTimeoutMs())
                // response timeout (tiempo total de espera de respuesta)
                .responseTimeout(Duration.ofMillis(props.getReadTimeoutMs()))
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(props.getReadTimeoutMs(), TimeUnit.MILLISECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(props.getReadTimeoutMs(), TimeUnit.MILLISECONDS)));

        // (Opcional) aumentar límite de tamaño de body si tu backend devuelve payloads grandes
        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(cfg -> cfg.defaultCodecs().maxInMemorySize(2 * 1024 * 1024)) // 2MB
                .build();

        return WebClient.builder()
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .exchangeStrategies(strategies)
                .build();
    }

    private String normalizeBaseUrl(String raw) {
        if (raw == null) return "";
        String s = raw.trim();
        // elimina slash final: "http://x:8080/" -> "http://x:8080"
        while (s.endsWith("/")) s = s.substring(0, s.length() - 1);
        return s;
    }
}
