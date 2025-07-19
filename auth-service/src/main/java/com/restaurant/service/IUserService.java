package com.restaurant.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.restaurant.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface IUserService {

    // ==================== USER DETAILS SERVICE METHODS ====================

    /**
     * Load user by email for Spring Security authentication
     * 
     * @param email The user's email
     * @return UserDetails for Spring Security
     * @throws UsernameNotFoundException if user not found
     */
    UserDetails loadUserByUsername(String email) throws UsernameNotFoundException;

    /**
     * Load user by email (custom method)
     * 
     * @param email The user's email
     * @return UserDetails for Spring Security
     * @throws UsernameNotFoundException if user not found
     */
    UserDetails loadUserByEmail(String email) throws UsernameNotFoundException;

    // ==================== USER CRUD OPERATIONS ====================

    /**
     * Find user by ID
     * 
     * @param userId The user ID
     * @return Optional of User entity
     */
    Optional<User> findById(Long userId);

    /**
     * Find user by email
     * 
     * @param email The user's email
     * @return Optional of User entity
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if user exists by email
     * 
     * @param email The user's email
     * @return true if user exists
     */
    boolean existsByEmail(String email);

    /**
     * Save user entity
     * 
     * @param user The user to save
     * @return Saved user entity
     */
    User saveUser(User user);

    /**
     * Get all users
     * 
     * @return List of all users
     */
    List<User> getAllUsers();

    /**
     * Get users by status
     * 
     * @param status The user status
     * @return List of users with specified status
     */
    List<User> getUsersByStatus(User.UserStatus status);

    // ==================== USER BLOCKING METHODS ====================

    /**
     * Block a user by storing their ID in Redis and updating DB status
     * 
     * @param userId The ID of the user to block
     * @param reason The reason for blocking (optional)
     * @throws RuntimeException if blocking fails
     */
    void blockUser(Long userId, String reason);

    /**
     * Unblock a user by removing from Redis and updating DB status
     * 
     * @param userId The ID of the user to unblock
     * @throws RuntimeException if unblocking fails
     */
    void unblockUser(Long userId);

    /**
     * Check if user is blocked in Redis cache
     * Falls back to database check if Redis is unavailable
     * 
     * @param userId The ID of the user to check
     * @return true if user is blocked, false otherwise
     */
    boolean isUserBlocked(Long userId);

    /**
     * Get the blocking reason for a user
     * 
     * @param userId The ID of the user
     * @return The blocking reason or default message
     */
    String getBlockingReason(Long userId);

    /**
     * Get all blocked user IDs from Redis
     * 
     * @return Set of blocked user keys
     * @throws RuntimeException if retrieval fails
     */
    Set<String> getAllBlockedUsers();

    /**
     * Sync Redis cache with database status
     * Ensures Redis cache is consistent with database
     */
    void syncBlockedUsersWithDatabase();

    /**
     * Block a user with default reason
     * 
     * @param userId The ID of the user to block
     */
    default void blockUser(Long userId) {
        blockUser(userId, "Blocked by admin");
    }

    /**
     * Check if user exists and is active in the system
     * 
     * @param userId The ID of the user to check
     * @return true if user exists and is active
     */
    boolean isUserActive(Long userId);

    /**
     * Get the current status of a user from database
     * 
     * @param userId The ID of the user
     * @return UserStatus enum value
     */
    User.UserStatus getUserStatus(Long userId);

    /**
     * Set user status in both Redis and database
     * 
     * @param userId The ID of the user
     * @param status The new status to set
     */
    void setUserStatus(Long userId, User.UserStatus status);

    // ==================== USER VALIDATION METHODS ====================

    /**
     * Validate user credentials for login
     * 
     * @param email    The user's email
     * @param password The raw password
     * @return true if credentials are valid
     */
    boolean validateUserCredentials(String email, String password);

    /**
     * Check if user can login (not blocked, active status)
     * 
     * @param userId The user ID
     * @return true if user can login
     */
    boolean canUserLogin(Long userId);

    /**
     * Update user last login timestamp
     * 
     * @param userId The user ID
     */
    void updateLastLogin(Long userId);
}
