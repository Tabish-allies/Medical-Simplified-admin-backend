package com.medicalSimplied.AdminPanel.config;

import com.medicalSimplied.AdminPanel.repository.AdminAuthInfoRepo;
import com.medicalSimplied.AdminPanel.security.JwtAuthenticationFilter;
import com.medicalSimplied.AdminPanel.security.JwtAuthEntryPoint;
import com.medicalSimplied.AdminPanel.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtils jwtUtils;
    private final AdminAuthInfoRepo adminRepo;
    private final JwtAuthEntryPoint jwtAuthEntryPoint; // 👈 new

    @Value("${jwt.header:Authorization}") private String headerName;
    @Value("${jwt.prefix:Bearer }") private String prefix;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        JwtAuthenticationFilter jwtFilter =
                new JwtAuthenticationFilter(jwtUtils, adminRepo, headerName, prefix);

        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/admin/login", "/api/admin/refresh").permitAll()
                .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(jwtAuthEntryPoint) // 👈 custom handler
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
