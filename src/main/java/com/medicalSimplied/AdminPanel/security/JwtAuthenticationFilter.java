// com.medicalSimplied.AdminPanel.security.JwtAuthenticationFilter.java
package com.medicalSimplied.AdminPanel.security;

import com.medicalSimplied.AdminPanel.repository.AdminAuthInfoRepo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final AdminAuthInfoRepo adminRepo;
    private final String headerName;
    private final String prefix;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        String header = request.getHeader(headerName);
        if (!StringUtils.hasText(header) || !header.startsWith(prefix)) {
            chain.doFilter(request, response);
            return;
        }

        String token = header.substring(prefix.length()).trim();
        try {
            Jws<Claims> jws = jwtUtils.parse(token);
            String username = jws.getBody().getSubject();

            // Optional: load admin to verify still active
            var adminOpt = adminRepo.findByUsername(username);
            if (adminOpt.isEmpty() || "INACTIVE".equalsIgnoreCase(adminOpt.get().getStatus())) {
                chain.doFilter(request, response);
                return;
            }

            var auth = new UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
            );
            SecurityContextHolder.getContext().setAuthentication(auth);
        } catch (Exception ignored) {
            // invalid token -> proceed without auth
        }

        chain.doFilter(request, response);
    }
}
