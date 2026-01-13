package com.tp.frontend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable);

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/do-login", "/css/**", "/js/**").permitAll()
                .requestMatchers("/vigilantes/me").hasRole("VIGILANTE")
                .requestMatchers("/users/**").hasAnyRole("ADMIN", "INVESTIGADOR")
                .anyRequest().authenticated()
        );

        // Si alguien no autenticado entra a /menu o cualquier ruta:
        http.exceptionHandling(ex -> ex
                .authenticationEntryPoint((req, res, e) -> res.sendRedirect("/login"))
        );

        http.logout(logout -> logout
                .logoutUrl("/logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .logoutSuccessUrl("/login?logout")
        );

        http.headers(headers -> headers.cacheControl(Customizer.withDefaults()));

        return http.build();
    }
}
