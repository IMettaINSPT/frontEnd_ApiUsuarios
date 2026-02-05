package com.tp.frontend.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tp.frontend.dto.Error.ApiError;
import com.tp.frontend.exception.ApiErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

public abstract class BaseApiClient {

    private static final Logger log = LoggerFactory.getLogger(BaseApiClient.class);

    /**
     * ObjectMapper "standalone" para parsear el body crudo cuando NO viene en formato ApiError.
     * findAndRegisterModules() ayuda con fechas/JavaTime si lo necesitás.
     */
    private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();

    /**
     * Timeouts: ajustalo a gusto. Esto cubre "no me responde" (back caído/lento).
     */
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(10);

    /**
     * Para no loguear bodies gigantes.
     */
    private static final int MAX_LOG_BODY_CHARS = 1200;

    protected final WebClient webClient;

    protected BaseApiClient(WebClient webClient) {
        this.webClient = webClient;
    }

    // =========================
    // API pública (protected)
    // =========================

    protected <T> T getList(String uri, String jwt, ParameterizedTypeReference<T> typeRef) {
        return execute(HttpMethod.GET, uri, jwt, null, typeRef).block();
    }

    protected <T> T get(String uri, String jwt, Class<T> responseType) {
        return execute(HttpMethod.GET, uri, jwt, null, responseType).block();
    }

    protected <T> T post(String uri, String jwt, Object body, Class<T> responseType) {
        return execute(HttpMethod.POST, uri, jwt, body, responseType).block();
    }

    protected <T> T put(String uri, String jwt, Object body, Class<T> responseType) {
        return execute(HttpMethod.PUT, uri, jwt, body, responseType).block();
    }

    protected void delete(String uri, String jwt) {
        executeVoid(HttpMethod.DELETE, uri, jwt).block();
    }

    // =========================
    // Internals
    // =========================

    private <T> Mono<T> execute(HttpMethod method,
                                String path,
                                String jwt,
                                Object body,
                                Class<T> responseType) {

        return webClient.method(method)
                .uri(path)
                .headers(h -> applyAuth(h, jwt))
                .contentType(body != null ? MediaType.APPLICATION_JSON : null)
                .body(body != null ? BodyInserters.fromValue(body) : BodyInserters.empty())
                .exchangeToMono(resp -> handleResponse(method, path, resp, responseType))
                .timeout(DEFAULT_TIMEOUT)
                .onErrorMap(ex -> mapTransportErrors(ex, method, path));
    }

    private <T> Mono<T> execute(HttpMethod method,
                                String path,
                                String jwt,
                                Object body,
                                ParameterizedTypeReference<T> typeRef) {

        return webClient.method(method)
                .uri(path)
                .headers(h -> applyAuth(h, jwt))
                .contentType(body != null ? MediaType.APPLICATION_JSON : null)
                .body(body != null ? BodyInserters.fromValue(body) : BodyInserters.empty())
                .exchangeToMono(resp -> handleResponse(method, path, resp, typeRef))
                .timeout(DEFAULT_TIMEOUT)
                .onErrorMap(ex -> mapTransportErrors(ex, method, path));
    }

    private Mono<Void> executeVoid(HttpMethod method, String path, String jwt) {
        return webClient.method(method)
                .uri(path)
                .headers(h -> applyAuth(h, jwt))
                .exchangeToMono(resp -> {
                    if (resp.statusCode().is2xxSuccessful()) {
                        return resp.releaseBody(); // consume + release
                    }
                    return resp.bodyToMono(String.class)
                            .defaultIfEmpty("")
                            .flatMap(raw -> {
                                ApiError apiError = toApiErrorOrFallback(resp, raw);
                                logBackendError(method, path, resp, apiError, raw);
                                return Mono.error(new ApiErrorException(apiError, resp.statusCode().value()));
                            });
                })
                .timeout(DEFAULT_TIMEOUT)
                .onErrorMap(ex -> mapTransportErrors(ex, method, path));
    }

    private <T> Mono<T> handleResponse(HttpMethod method, String path, ClientResponse resp, Class<T> responseType) {
        if (resp.statusCode().is2xxSuccessful()) {
            // Si el endpoint devuelve vacío (204) y pedís un tipo, esto puede explotar.
            // Si querés soportar 204, definí responseType=Void y manejalo arriba.
            return resp.bodyToMono(responseType);
        }

        return resp.bodyToMono(String.class)
                .defaultIfEmpty("")
                .flatMap(raw -> {
                    ApiError apiError = toApiErrorOrFallback(resp, raw);
                    logBackendError(method, path, resp, apiError, raw);
                    return Mono.error(new ApiErrorException(apiError, resp.statusCode().value()));
                });
    }

    private <T> Mono<T> handleResponse(HttpMethod method, String path, ClientResponse resp, ParameterizedTypeReference<T> typeRef) {
        if (resp.statusCode().is2xxSuccessful()) {
            return resp.bodyToMono(typeRef);
        }

        return resp.bodyToMono(String.class)
                .defaultIfEmpty("")
                .flatMap(raw -> {
                    ApiError apiError = toApiErrorOrFallback(resp, raw);
                    logBackendError(method, path, resp, apiError, raw);
                    return Mono.error(new ApiErrorException(apiError, resp.statusCode().value()));
                });
    }

    private ApiError toApiErrorOrFallback(ClientResponse resp, String rawBody) {
        Optional<ApiError> parsed = tryParseApiError(rawBody);

        if (parsed.isPresent()) {
            // Si por algún motivo message viene null, igual ponemos algo útil.
            ApiError e = parsed.get();
            if (e.getMessage() == null || e.getMessage().isBlank()) {
                e.setMessage("Error del servidor");
            }
            return e;
        }

        // Fallback cuando vino HTML/texto/otro JSON/no body
        ApiError fallback = new ApiError();
        fallback.setTimestamp(OffsetDateTime.now().toString());
        fallback.setStatus(String.valueOf(resp.statusCode().value()));

        int code = resp.statusCode().value();
        if (code == 401) fallback.setMessage("Tu sesión venció o no es válida. Iniciá sesión nuevamente.");
        else if (code == 403) fallback.setMessage("No tenés permisos para realizar esta acción.");
        else if (code == 404) fallback.setMessage("No se encontró el recurso solicitado.");
        else if (code >= 400 && code < 500) fallback.setMessage("No se pudo procesar la solicitud. Revisá los datos e intentá de nuevo.");
        else fallback.setMessage("Ocurrió un problema en el servidor. Intentá más tarde.");

        // NO lo muestres al usuario; queda para log/debug.
        String snippet = safeSnippet(rawBody);
        fallback.setTrace(snippet.isBlank() ? "Respuesta sin body o no parseable a ApiError" : "Respuesta no-ApiError: " + snippet);

        return fallback;
    }

    private Optional<ApiError> tryParseApiError(String rawBody) {
        if (rawBody == null || rawBody.isBlank()) return Optional.empty();
        try {
            return Optional.of(MAPPER.readValue(rawBody, ApiError.class));
        } catch (Exception ignored) {
            return Optional.empty();
        }
    }

    private void logBackendError(HttpMethod method, String path, ClientResponse resp, ApiError apiError, String rawBody) {
        MediaType ct = resp.headers().contentType().orElse(null);

        // Importante: si el backend te devuelve HTML, esto te sirve muchísimo para diagnosticar.
        log.error("BACKEND_ERROR method={} path={} status={} contentType={} apiMessage='{}' rawSnippet='{}'",
                method,
                path,
                resp.statusCode().value(),
                ct,
                apiError != null ? apiError.getMessage() : null,
                safeSnippet(rawBody)
        );
    }

    private Throwable mapTransportErrors(Throwable ex, HttpMethod method, String path) {
        // Timeout (no hubo respuesta HTTP)
        if (ex instanceof TimeoutException) {
            // Lo convertimos a WebClientRequestException para que caiga en tu handler amigable
            return new WebClientRequestException(ex, method, safeUri(path), HttpHeaders.EMPTY);
        }

        // Si ya es de transporte, lo dejamos (tu GlobalExceptionHandler ya lo maneja)
        if (ex instanceof WebClientRequestException) {
            return ex;
        }

        // Otros errores inesperados (codec, mapping, etc.) los logueamos igual
        log.error("FRONT_CLIENT_UNEXPECTED method={} path={} exClass={} msg={}",
                method, path, ex.getClass().getName(), ex.getMessage(), ex);

        return ex;
    }

    private URI safeUri(String path) {
        try {
            return URI.create(path);
        } catch (Exception e) {
            return URI.create("/");
        }
    }

    private String safeSnippet(String rawBody) {
        if (rawBody == null) return "";
        String s = rawBody.replaceAll("\\s+", " ").trim();
        if (s.length() <= MAX_LOG_BODY_CHARS) return s;
        return s.substring(0, MAX_LOG_BODY_CHARS) + "...(truncated)";
    }

    protected void applyAuth(HttpHeaders headers, String jwt) {
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        if (jwt != null && !jwt.isBlank()) {
            headers.setBearerAuth(jwt);
        }
    }
}