package com.ghoul.AmanaFund.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private final JwtFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    public SecurityConfig(JwtFilter jwtAuthFilter, AuthenticationProvider authenticationProvider) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.authenticationProvider = authenticationProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
               //    .cors(withDefaults()) // Permet l'utilisation des CORS
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(req -> req
                        // Désactiver la sécurité temporairement pour toutes les routes
                        .anyRequest().permitAll()
                )
             //   .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
        // Supprimer le filtre JWT
        // .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
        ;

        return http.build();// Désactive la protection CSRF (recommandée pour une API stateless)
            /*    .authorizeHttpRequests(req -> req
                        // Routes Swagger accessibles sans authentification
                        .requestMatchers(
                                "/v2/api-docs",
                                "/v3/api-docs",
                                "/swagger-resources",
                                "/swagger-resources/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/configuration/ui",
                                "/configuration/security",
                                "/webjars/**"
                        ).permitAll()  // Permet d'accéder à ces routes sans authentification

                        // Autres routes sécurisées
                        .anyRequest().authenticated()  // Demande une authentification pour les autres routes
                )
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))  // Utilisation de JWT, pas de session
                .authenticationProvider(authenticationProvider)  // Ajout du provider d'authentification
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);  // Filtre JWT avant le filtre d'authentification par mot de passe

        return http.build();*/
    }
}
