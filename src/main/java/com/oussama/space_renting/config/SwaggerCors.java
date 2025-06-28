package com.oussama.space_renting.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SwaggerCors {

    @Bean
    public WebMvcConfigurer swaggerCorsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/v3/api-docs/**")
                        .allowedOrigins("*")
                        .allowedMethods("*");
                registry.addMapping("/swagger-ui/**")
                        .allowedOrigins("*")
                        .allowedMethods("*");
                registry.addMapping("/v3/api-docs/swagger-config")
                        .allowedOrigins("*")
                        .allowedMethods("*");
                registry.addMapping("/swagger-resources/*")
                        .allowedOrigins("*")
                        .allowedMethods("*");
            }
        };
    }

}
