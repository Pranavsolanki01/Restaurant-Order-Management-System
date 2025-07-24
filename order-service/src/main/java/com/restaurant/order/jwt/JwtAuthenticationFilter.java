package com.restaurant.order.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        try {
            String authHeader = request.getHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.debug("No JWT token found in request headers");
                filterChain.doFilter(request, response);
                return;
            }

            String token = authHeader.substring(7);

            if (!jwtUtil.validateToken(token)) {
                log.warn("Invalid JWT token");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"error\":\"Invalid or expired token\"}");
                response.setContentType("application/json");
                return;
            }

            // Extract user details from token
            String email = jwtUtil.extractEmail(token);
            Long userId = jwtUtil.extractUserId(token);
            String role = jwtUtil.extractRole(token);

            // Create authorities
            List<SimpleGrantedAuthority> authorities = List.of(
                    new SimpleGrantedAuthority("ROLE_" + (role != null ? role : "USER")));

            // Create authentication object
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(email, null,
                    authorities);

            // Add custom details
            JwtAuthenticationDetails details = new JwtAuthenticationDetails(email, userId, role);
            authentication.setDetails(details);

            // Set authentication in security context
            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.debug("JWT authentication successful for user: {} with role: {}", email, role);

        } catch (Exception e) {
            log.error("JWT authentication failed: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\":\"Authentication failed\"}");
            response.setContentType("application/json");
            return;
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        String path = request.getRequestURI();
        // Skip JWT filter for public endpoints
        return path.startsWith("/api/actuator/") ||
                path.startsWith("/api/swagger-ui/") ||
                path.startsWith("/api/v3/api-docs/") ||
                path.equals("/api/swagger-ui.html");
    }

    // Inner class to hold JWT details
    public static class JwtAuthenticationDetails {
        private final String email;
        private final Long userId;
        private final String role;

        public JwtAuthenticationDetails(String email, Long userId, String role) {
            this.email = email;
            this.userId = userId;
            this.role = role;
        }

        public String getEmail() {
            return email;
        }

        public Long getUserId() {
            return userId;
        }

        public String getRole() {
            return role;
        }

        public boolean isAdmin() {
            return "ADMIN".equals(role);
        }
    }
}
