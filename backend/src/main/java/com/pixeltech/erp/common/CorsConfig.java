package com.pixeltech.erp.common;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Allows the React frontend (served by nginx) to call the backend API.
 * Origins are configurable via the CORS_ALLOWED_ORIGINS env var.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        String origins = System.getenv().getOrDefault("CORS_ALLOWED_ORIGINS", "*");
        registry.addMapping("/api/**")
                .allowedOrigins(origins.split(","))
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }
}
