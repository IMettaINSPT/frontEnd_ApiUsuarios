package com.tp.frontend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // Para TP, lo desactivamos y evitamos lidiar con CSRF en forms
                .csrf(csrf -> csrf.disable())

                // Permitir login + estáticos. Proteger el resto.
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/do-login","/css/**", "/js/**").permitAll()
                        .anyRequest().authenticated()
                )

                // Usar TU página /login como login page (GET)
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/home", true)
                        .permitAll()
                )
                // Logout estándar: invalida sesión y borra cookie
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .logoutSuccessUrl("/login?logout")
                )

                // 🔥 CLAVE: evita ver páginas cacheadas al volver "Atrás"
                .headers(headers -> headers
                        .cacheControl(Customizer.withDefaults())
                        .cacheControl(withDefaults())
                        .frameOptions(frame -> frame.sameOrigin())
                );

        return http.build();
    }
}
