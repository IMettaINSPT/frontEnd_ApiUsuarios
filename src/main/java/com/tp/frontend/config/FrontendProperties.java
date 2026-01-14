package com.tp.frontend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "frontend")
public class FrontendProperties {

    /**
     * Base URL del backend (ej: http://localhost:8080)
     */
    private String backendBaseUrl;

    /**
     * URL pública del frontend (opcional, útil para links absolutos)
     * ej: http://localhost:8081
     */
    private String publicUrl;

    public String getBackendBaseUrl() {
        return backendBaseUrl;
    }

    public void setBackendBaseUrl(String backendBaseUrl) {
        this.backendBaseUrl = backendBaseUrl;
    }

    public String getPublicUrl() {
        return publicUrl;
    }

    public void setPublicUrl(String publicUrl) {
        this.publicUrl = publicUrl;
    }
}
