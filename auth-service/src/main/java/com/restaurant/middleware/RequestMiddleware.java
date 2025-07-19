package com.restaurant.middleware;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

/**
 * Simple consolidated middleware for CORS, Logging, and Rate Limiting
 */
@Component
@Order(1)
@Slf4j
@RequiredArgsConstructor
public class RequestMiddleware extends OncePerRequestFilter {

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${app.rate-limit.default-limit:100}")
    private int defaultLimit;

    @Value("${app.rate-limit.login-limit:5}")
    private int loginLimit;

    @Value("${app.rate-limit.register-limit:3}")
    private int registerLimit;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        long startTime = Instant.now().toEpochMilli();

        // 1. Add CORS headers
        addCorsHeaders(response);

        // 2. Handle preflight requests
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        // 3. Log request
        logRequest(request);

        // 4. Check rate limiting for API endpoints
        if (request.getRequestURI().startsWith("/api/") && isRateLimited(request)) {
            handleRateLimit(response);
            return;
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            // 5. Log response
            long duration = Instant.now().toEpochMilli() - startTime;
            logResponse(request, response, duration);
        }
    }

    private void addCorsHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, X-Requested-With");
        response.setHeader("Access-Control-Max-Age", "3600");
    }

    private void logRequest(HttpServletRequest request) {
        log.info("REQUEST: {} {} from {}",
                request.getMethod(),
                request.getRequestURI(),
                request.getRemoteAddr());
    }

    private void logResponse(HttpServletRequest request, HttpServletResponse response, long duration) {
        log.info("RESPONSE: {} {} - Status: {} - Duration: {}ms",
                request.getMethod(),
                request.getRequestURI(),
                response.getStatus(),
                duration);
    }

    private boolean isRateLimited(HttpServletRequest request) {
        try {
            String clientIp = request.getRemoteAddr();
            String endpoint = getEndpointKey(request);
            String key = "rate_limit:" + clientIp + ":" + endpoint;
            int limit = getEndpointLimit(request);

            String current = redisTemplate.opsForValue().get(key);
            int currentCount = current != null ? Integer.parseInt(current) : 0;

            if (currentCount >= limit) {
                log.warn("Rate limit exceeded for {} from {}", endpoint, clientIp);
                return true;
            }

            if (current == null) {
                redisTemplate.opsForValue().set(key, "1", Duration.ofMinutes(1));
            } else {
                redisTemplate.opsForValue().increment(key);
            }

            return false;
        } catch (Exception e) {
            log.error("Error checking rate limit: {}", e.getMessage());
            return false; // Allow request if Redis is down
        }
    }

    private String getEndpointKey(HttpServletRequest request) {
        String uri = request.getRequestURI();
        if (uri.contains("/login"))
            return "login";
        if (uri.contains("/register"))
            return "register";
        return "api";
    }

    private int getEndpointLimit(HttpServletRequest request) {
        String uri = request.getRequestURI();
        if (uri.contains("/login"))
            return loginLimit;
        if (uri.contains("/register"))
            return registerLimit;
        return defaultLimit;
    }

    private void handleRateLimit(HttpServletResponse response) throws IOException {
        response.setStatus(429);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"Rate limit exceeded\",\"message\":\"Too many requests\"}");
    }
}
