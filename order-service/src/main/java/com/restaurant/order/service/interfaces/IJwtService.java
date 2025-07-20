package com.restaurant.order.service.interfaces;

public interface IJwtService {

    /**
     * Get current authenticated user's email from JWT token
     * 
     * @return User email
     */
    String getCurrentUserEmail();

    /**
     * Get current authenticated user's ID from JWT token
     * 
     * @return User ID
     */
    Long getCurrentUserId();

    /**
     * Get current authenticated user's role from JWT token
     * 
     * @return User role
     */
    String getCurrentUserRole();

    /**
     * Check if the given user ID matches the current authenticated user
     * 
     * @param userId User ID to check
     * @return true if matches, false otherwise
     */
    boolean isCurrentUser(Long userId);

    /**
     * Check if the current authenticated user is an admin
     * 
     * @return true if admin, false otherwise
     */
    boolean isAdmin();
}
