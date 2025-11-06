package com.medicalSimplied.AdminPanel.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {

                registry.addMapping("/**")  // apply CORS to all endpoints
                        // ✅ Allowed origins (your real production + dev)
                        .allowedOrigins(
                                "http://localhost:3000",        // Local dev
                                "https://admin.medicalsimplified.com", // Production Flutter Web
                                "http://localhost:12345",              // Flutter Web (local dev)
                                "http://127.0.0.1:12345"
                        )
                        // ✅ Allowed methods
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        // ✅ Allowed headers (important for JSON, Authorization)
                        .allowedHeaders("*")
                        // ✅ Expose headers back to the client (optional)
                        .exposedHeaders("Authorization")
                        // ✅ Allow credentials (JWT, cookies)
                        .allowCredentials(true)
                        // ✅ Cache preflight requests for 1 hour (optimization)
                        .maxAge(3600);
            }
        };
    }
}
