package com.ghoul.AmanaFund.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:4200") // Ajoutez ici l'URL de votre application front-end si elle est différente
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Vous pouvez ajouter d'autres méthodes HTTP si nécessaire
                .allowedHeaders("*"); // Permet tous les headers (ou vous pouvez restreindre à certains headers spécifiques)
    }
}
