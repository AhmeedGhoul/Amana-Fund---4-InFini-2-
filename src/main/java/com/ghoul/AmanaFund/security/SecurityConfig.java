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
        http.cors(withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(req -> req
                        // Public endpoints (authentication-related only)
                        .requestMatchers(
                                "/auth/register",
                                "/auth/authenticate",
                                "/auth/Promote",
                                "/auth/forgot-password",
                                "/auth/reset-password",
                                "/auth/F2A",
                                "/v2/api-docs",
                                "/v3/api-docs/**",
                                "/Agency/**",
                                "/swagger-resources/**",
                                "/configuration/ui",
                                "/configuration/security",
                                "/swagger-ui/**",
                                "/webjars/**",
                                "/swagger-ui.html",
                                "/Request/**"

                        ).permitAll()

                        // ADMIN-only secured endpoints
                        .requestMatchers(
                                "/auth/Promote",
                                "/auth/Demote",
                                "/auth/Modify",
                                "/auth/Delete/**",
                                "/auth/users",
                                "/auth/generateUserReport",
                                "/ActivityLog/**",
                                "/audit/**",
                                "/contracts/**",
                                "/case/**",
                                "/Garantie/**",
                                "/Payment/**",
                                "/Police/**",
                                "/Sinistre/**",
                                "/AccountPayment/**",
                                "/Account/**",
                                "/CreditPool/**"
                        ).hasRole("ADMIN")

                        // AGENT-only secured endpoints
                        .requestMatchers(
                                "/Request/**",
                                "/account-payments/**",
                                "/Contract/**",
                                "/Object/**",
                                "/Person/**",
                                "/Sinitre/**"
                        ).hasRole("AGENT")

                        // AUDITOR-only secured endpoints
                        .requestMatchers(
                                "/audit/**",
                                "/ActivityLog/**"
                        ).hasRole("AUDITOR")

                        // Any other requests must be authenticated
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
