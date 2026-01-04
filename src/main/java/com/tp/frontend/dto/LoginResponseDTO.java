package com.tp.frontend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginResponseDTO {

    @JsonProperty("accessToken")
    private String accessToken;

    private String tokenType;
    private String username;
    private String rol;

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}
