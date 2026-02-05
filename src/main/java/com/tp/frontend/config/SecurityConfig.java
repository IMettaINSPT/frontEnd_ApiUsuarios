package com.tp.frontend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // ---------------------------------
                // CSRF
                // ---------------------------------
                .csrf(csrf -> csrf.disable()) // OK para MVC + API backend separado

                // ---------------------------------
                // AUTORIZACIÓN
                // ---------------------------------
                .authorizeHttpRequests(auth -> auth
                        // Públicos
                        .requestMatchers(
                                "/login",
                                "/logout-success",
                                "/error",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/favicon.ico"
                        ).permitAll()

                        // Perfil propio
                        .requestMatchers("/users/me").authenticated()

                        // Administración
                        .requestMatchers("/users/**").hasRole("ADMIN")

                        // Todo lo demás requiere login
                        .anyRequest().authenticated()
                )

                // ---------------------------------
                // LOGIN
                // ---------------------------------
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/login?error")
                        .permitAll()
                )

                // ---------------------------------
                // LOGOUT
                // ---------------------------------
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )

                // ---------------------------------
                // SESIÓN
                // ---------------------------------
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )

                // ---------------------------------
                // ACCESO DENEGADO (opcional, PRO)
                // ---------------------------------
                .exceptionHandling(ex -> ex
                        .accessDeniedPage("/") // o "/error/403" si querés
                );

        return http.build();
    }
}
