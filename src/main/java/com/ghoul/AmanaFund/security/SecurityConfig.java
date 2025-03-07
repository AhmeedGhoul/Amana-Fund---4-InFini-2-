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
//        http.
//                cors(withDefaults()).
//                csrf(AbstractHttpConfigurer::disable).authorizeHttpRequests(req -> req
//                        .requestMatchers("/auth/register",
//                                "/auth/authenticate",
//                                "/contracts/**",
//                                "/creditpool/**",
//                                "/auth/forgot-password",
//                                "/auth/reset-password",
//                                "/auth/F2A",
//                                "/auth/Promote",
//                                "/v2/api-docs",
//                                "/v3/api-docs",
//                                "/v3/api-docs/**",
//                                "/swagger-resources",
//                                "/swagger-resources/**",
//                                "/configuration/ui",
//                                "/configuration/security",
//                                "/swagger-ui/**",
//                                "/webjars/**",
//                                "/swagger-ui.html").permitAll()
//                        .requestMatchers(
//                                "/ActivityLog/**",
//                                "/audit/**",
//                                "/contracts/**",
//                                "/creditpool/**",
//                                "/AccountPayment/**",
//                                "/Contract/**",
//                                "/case/**",
//                                "/Garantie/**",
//                                "/Payment/**",
//                                "/Police/**",
//                                "/Sinistre/**"
//                        ).hasRole("AUDITOR")
//                        .requestMatchers(
//                                "Account/**",
//                                "Request/**",
//                                "account-payments/**",
//                                "Contract/**",
//                                "Garantie/**",
//                                "Object/**",
//                                "Person/**",
//                                "Police/**",
//                                "Sinitre/**",
//                                "/AccountPayment/**",
//                                "/Account/**",
//                                "/Agency/**",
//                                "/Contract/**",
//                                "/CreditPool/**",
//                                "/Garantie/**",
//                                "/Request/**"
//                        ).hasRole("AGENT")
//                        .requestMatchers("/**").hasRole("ADMIN")
//                        .anyRequest().authenticated()
//        )
//        .authenticationProvider(authenticationProvider)
//                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
        http.
                cors(withDefaults()).
                csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(req -> req
                        .requestMatchers("/**", "/v2/api-docs",
                                "/v3/api-docs",
                                "/v3/api-docs/**",
                                "/swagger-resources",
                                "/swagger-resources/**",
                                "/configuration/ui",
                                "/configuration/security",
                                "/swagger-ui/**",
                                "/webjars/**",
                                "/swagger-ui.html").permitAll()
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
