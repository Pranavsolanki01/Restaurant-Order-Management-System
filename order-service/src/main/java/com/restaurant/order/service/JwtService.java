package com.restaurant.order.service;

import com.restaurant.order.service.interfaces.IJwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class JwtService implements IJwtService {

    @Override
    public String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getClaimAsString("email");
        }
        throw new RuntimeException("User is not authenticated");
    }

    @Override
    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            Object userIdClaim = jwt.getClaim("userId");
            if (userIdClaim instanceof Long) {
                return (Long) userIdClaim;
            } else if (userIdClaim instanceof Integer) {
                return ((Integer) userIdClaim).longValue();
            } else if (userIdClaim instanceof String) {
                try {
                    return Long.parseLong((String) userIdClaim);
                } catch (NumberFormatException e) {
                    log.error("Cannot parse userId from JWT: {}", userIdClaim);
                    throw new RuntimeException("Invalid userId in JWT token");
                }
            }
        }
        throw new RuntimeException("User is not authenticated");
    }

    @Override
    public String getCurrentUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getClaimAsString("role");
        }
        throw new RuntimeException("User is not authenticated");
    }

    @Override
    public boolean isCurrentUser(Long userId) {
        try {
            return getCurrentUserId().equals(userId);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean isAdmin() {
        try {
            String role = getCurrentUserRole();
            return "ADMIN".equalsIgnoreCase(role);
        } catch (Exception e) {
            return false;
        }
    }
}
