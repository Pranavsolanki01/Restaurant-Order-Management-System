package com.restaurant.payment.jwt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JwtUserContext {

    public JwtAuthenticationFilter.JwtAuthenticationDetails getCurrentUserDetails() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null
                    && authentication.getDetails() instanceof JwtAuthenticationFilter.JwtAuthenticationDetails) {
                return (JwtAuthenticationFilter.JwtAuthenticationDetails) authentication.getDetails();
            }
        } catch (Exception e) {
            log.warn("Failed to get current user details: {}", e.getMessage());
        }
        return null;
    }

    public Long getCurrentUserId() {
        JwtAuthenticationFilter.JwtAuthenticationDetails details = getCurrentUserDetails();
        return details != null ? details.getUserId() : null;
    }

    public String getCurrentUserEmail() {
        JwtAuthenticationFilter.JwtAuthenticationDetails details = getCurrentUserDetails();
        return details != null ? details.getEmail() : null;
    }

    public String getCurrentUserRole() {
        JwtAuthenticationFilter.JwtAuthenticationDetails details = getCurrentUserDetails();
        return details != null ? details.getRole() : null;
    }

    public boolean isCurrentUserAdmin() {
        JwtAuthenticationFilter.JwtAuthenticationDetails details = getCurrentUserDetails();
        return details != null && details.isAdmin();
    }

    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal());
    }
}
