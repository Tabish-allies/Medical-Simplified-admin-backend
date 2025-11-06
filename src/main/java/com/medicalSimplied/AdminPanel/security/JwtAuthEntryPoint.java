package com.medicalSimplied.AdminPanel.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medicalSimplied.AdminPanel.util.GlobalResponse;
import com.medicalSimplied.AdminPanel.util.ResponseBuilder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper; // ✅ use Spring-managed mapper

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        GlobalResponse<Object> errorResponse = ResponseBuilder.failure(
                HttpStatus.UNAUTHORIZED,
                "Unauthorized",
                request.getRequestURI(),
                "JWT expired or invalid"
        );

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }
}
