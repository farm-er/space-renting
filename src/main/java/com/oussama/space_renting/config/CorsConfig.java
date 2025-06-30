package com.oussama.space_renting.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // API CORS configuration
        registry.addMapping("/api/v1/**")
                .allowedOrigins("http://localhost:8080")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);

        // Swagger UI CORS configuration
        registry.addMapping("/v3/api-docs/**")
                .allowedOrigins("*")
                .allowedMethods("*")
                .allowedHeaders("*");

        registry.addMapping("/swagger-ui/**")
                .allowedOrigins("*")
                .allowedMethods("*")
                .allowedHeaders("*");

        registry.addMapping("/swagger-ui.html")
                .allowedOrigins("*")
                .allowedMethods("*")
                .allowedHeaders("*");

        registry.addMapping("/swagger-resources/**")
                .allowedOrigins("*")
                .allowedMethods("*")
                .allowedHeaders("*");
    }
}
