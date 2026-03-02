package com.tp.frontend.service;

import com.tp.frontend.client.AuthApiClient;
import com.tp.frontend.dto.Login.LoginRequest;
import com.tp.frontend.dto.Login.LoginResponse;
import com.tp.frontend.dto.User.UserResponse; // 🟢 Cambio: Usamos el DTO unificado
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthApiClient authApiClient;

    public AuthService(AuthApiClient authApiClient) {
        this.authApiClient = authApiClient;
    }

    /**
     * Realiza el login enviando usuario y contraseña individuales.
     */
    public LoginResponse login(String username, String password) {
        // Se crea el LoginRequest que espera el AuthApiClient (y el Backend)
        return authApiClient.login(new LoginRequest(username, password));
    }

    /**
     * Sobrecarga para procesar el objeto LoginRequest directamente desde el controlador.
     */
    public LoginResponse login(LoginRequest req) {
        return login(req.getUsername(), req.getPassword());
    }

    /**
     * Obtiene el perfil del usuario autenticado.
     * @return UserResponse (con campo 'rol', 'vigilanteId', etc.)
     */
    public UserResponse me(String jwt) {
        // 🟢 Cambio: Ahora devuelve UserResponse para ser coherente con el AuthApiClient
        return authApiClient.me(jwt);
    }
}