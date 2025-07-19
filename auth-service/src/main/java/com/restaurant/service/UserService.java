package com.restaurant.service;

import com.restaurant.entity.User;
import com.restaurant.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements IUserService, UserDetailsService {

    private final UserRepository userRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final PasswordEncoder passwordEncoder;
    
    private static final String BLOCKED_USER_PREFIX = "blocked_user:";
    private static final String BLOCKED_REASON_PREFIX = "blocked_reason:";
    private static final String LAST_LOGIN_PREFIX = "last_login:";

    // ==================== USER DETAILS SERVICE METHODS ====================

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return loadUserByEmail(email);
    }

    @Override
    public UserDetails loadUserByEmail(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // Check if user is blocked before creating UserDetails
        if (isUserBlocked(user.getId())) {
            throw new UsernameNotFoundException("User is blocked");
        }

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.getStatus() == User.UserStatus.ACTIVE, // enabled
                true, // accountNonExpired
                true, // credentialsNonExpired
                user.getStatus() != User.UserStatus.BLOCKED, // accountNonLocked
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }

    // ==================== USER CRUD OPERATIONS ====================

    @Override
    public Optional<User> findById(Long userId) {
        return userRepository.findById(userId);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public List<User> getUsersByStatus(User.UserStatus status) {
        return userRepository.findByStatus(status);
    }

    // ==================== USER BLOCKING METHODS ====================

    @Override
    public void blockUser(Long userId, String reason) {
        try {
            // Store user ID in Redis
            String key = BLOCKED_USER_PREFIX + userId;
            String reasonKey = BLOCKED_REASON_PREFIX + userId;
            
            redisTemplate.opsForValue().set(key, "BLOCKED");
            redisTemplate.opsForValue().set(reasonKey, reason != null ? reason : "Blocked by admin");
            
            // Update user status in database
            userRepository.findById(userId).ifPresent(user -> {
                user.setStatus(User.UserStatus.BLOCKED);
                userRepository.save(user);
                log.info("User {} has been blocked. Reason: {}", userId, reason);
            });
            
        } catch (Exception e) {
            log.error("Error blocking user {}: {}", userId, e.getMessage());
            throw new RuntimeException("Failed to block user");
        }
    }

    @Override
    public void unblockUser(Long userId) {
        try {
            String key = BLOCKED_USER_PREFIX + userId;
            String reasonKey = BLOCKED_REASON_PREFIX + userId;
            
            // Remove from Redis
            redisTemplate.delete(key);
            redisTemplate.delete(reasonKey);
            
            // Update user status in database
            userRepository.findById(userId).ifPresent(user -> {
                user.setStatus(User.UserStatus.ACTIVE);
                userRepository.save(user);
                log.info("User {} has been unblocked", userId);
            });
            
        } catch (Exception e) {
            log.error("Error unblocking user {}: {}", userId, e.getMessage());
            throw new RuntimeException("Failed to unblock user");
        }
    }

    @Override
    public boolean isUserBlocked(Long userId) {
        try {
            String key = BLOCKED_USER_PREFIX + userId;
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            log.error("Error checking blocked status for user {}: {}", userId, e.getMessage());
            // If Redis is down, fall back to database check
            return userRepository.findById(userId)
                .map(user -> user.getStatus() == User.UserStatus.BLOCKED)
                .orElse(false);
        }
    }

    @Override
    public String getBlockingReason(Long userId) {
        try {
            String reasonKey = BLOCKED_REASON_PREFIX + userId;
            String reason = redisTemplate.opsForValue().get(reasonKey);
            return reason != null ? reason : "Blocked by admin";
        } catch (Exception e) {
            log.error("Error getting blocking reason for user {}: {}", userId, e.getMessage());
            return "Blocked by admin";
        }
    }

    @Override
    public Set<String> getAllBlockedUsers() {
        try {
            return redisTemplate.keys(BLOCKED_USER_PREFIX + "*");
        } catch (Exception e) {
            log.error("Error getting all blocked users: {}", e.getMessage());
            throw new RuntimeException("Failed to retrieve blocked users");
        }
    }

    @Override
    public void syncBlockedUsersWithDatabase() {
        try {
            // Get all blocked users from database
            userRepository.findByStatus(User.UserStatus.BLOCKED).forEach(user -> {
                String key = BLOCKED_USER_PREFIX + user.getId();
                if (!Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
                    redisTemplate.opsForValue().set(key, "BLOCKED");
                    redisTemplate.opsForValue().set(BLOCKED_REASON_PREFIX + user.getId(), "Blocked by admin");
                }
            });
            
            log.info("Synced blocked users with database");
        } catch (Exception e) {
            log.error("Error syncing blocked users with database: {}", e.getMessage());
        }
    }

    @Override
    public boolean isUserActive(Long userId) {
        return userRepository.findById(userId)
            .map(user -> user.getStatus() == User.UserStatus.ACTIVE)
            .orElse(false);
    }

    @Override
    public User.UserStatus getUserStatus(Long userId) {
        return userRepository.findById(userId)
            .map(User::getStatus)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public void setUserStatus(Long userId, User.UserStatus status) {
        try {
            userRepository.findById(userId).ifPresent(user -> {
                user.setStatus(status);
                userRepository.save(user);
                
                // Update Redis accordingly
                String key = BLOCKED_USER_PREFIX + userId;
                String reasonKey = BLOCKED_REASON_PREFIX + userId;
                
                if (status == User.UserStatus.BLOCKED) {
                    redisTemplate.opsForValue().set(key, "BLOCKED");
                    redisTemplate.opsForValue().set(reasonKey, "Blocked by admin");
                } else {
                    redisTemplate.delete(key);
                    redisTemplate.delete(reasonKey);
                }
                
                log.info("User {} status updated to {}", userId, status);
            });
        } catch (Exception e) {
            log.error("Error setting user status for {}: {}", userId, e.getMessage());
            throw new RuntimeException("Failed to set user status");
        }
    }

    // ==================== USER VALIDATION METHODS ====================

    @Override
    public boolean validateUserCredentials(String email, String password) {
        return userRepository.findByEmail(email)
            .map(user -> passwordEncoder.matches(password, user.getPassword()))
            .orElse(false);
    }

    @Override
    public boolean canUserLogin(Long userId) {
        return userRepository.findById(userId)
            .map(user -> user.getStatus() == User.UserStatus.ACTIVE && !isUserBlocked(userId))
            .orElse(false);
    }

    @Override
    public void updateLastLogin(Long userId) {
        try {
            String lastLoginKey = LAST_LOGIN_PREFIX + userId;
            redisTemplate.opsForValue().set(lastLoginKey, ZonedDateTime.now(ZoneId.systemDefault()).toString());
            
            // Also update in database if needed
            userRepository.findById(userId).ifPresent(user -> {
                user.setUpdatedAt(ZonedDateTime.now(ZoneId.systemDefault()));
                userRepository.save(user);
            });
            
            log.debug("Updated last login for user: {}", userId);
        } catch (Exception e) {
            log.error("Error updating last login for user {}: {}", userId, e.getMessage());
        }
    }
}
