package com.restaurant.order.service;

import com.restaurant.order.jwt.JwtAuthenticationFilter;
import com.restaurant.order.service.interfaces.IJwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class JwtService implements IJwtService {

    @Override
    public String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getDetails() instanceof JwtAuthenticationFilter.JwtAuthenticationDetails) {
            JwtAuthenticationFilter.JwtAuthenticationDetails details = 
                (JwtAuthenticationFilter.JwtAuthenticationDetails) authentication.getDetails();
            return details.getEmail();
        }
        throw new RuntimeException("User is not authenticated");
    }

    @Override
    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getDetails() instanceof JwtAuthenticationFilter.JwtAuthenticationDetails) {
            JwtAuthenticationFilter.JwtAuthenticationDetails details = 
                (JwtAuthenticationFilter.JwtAuthenticationDetails) authentication.getDetails();
            return details.getUserId();
        }
        throw new RuntimeException("User is not authenticated");
    }

    @Override
    public String getCurrentUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getDetails() instanceof JwtAuthenticationFilter.JwtAuthenticationDetails) {
            JwtAuthenticationFilter.JwtAuthenticationDetails details = 
                (JwtAuthenticationFilter.JwtAuthenticationDetails) authentication.getDetails();
            return details.getRole();
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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getDetails() instanceof JwtAuthenticationFilter.JwtAuthenticationDetails) {
            JwtAuthenticationFilter.JwtAuthenticationDetails details = 
                (JwtAuthenticationFilter.JwtAuthenticationDetails) authentication.getDetails();
            return details.isAdmin();
        }
        return false;
    }

    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated();
    }
}
