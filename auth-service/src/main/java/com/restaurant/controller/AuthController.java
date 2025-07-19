package com.restaurant.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.restaurant.dto.*;
import com.restaurant.entity.User;
import com.restaurant.service.IAuthService;
import com.restaurant.service.IUserService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final IAuthService authService;
    private final IUserService userService;

    @PostMapping("/register")
    public ResponseEntity<JwtResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            JwtResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            String message = e.getMessage();

            // Check if it's a user blocked error
            if (message != null && message.contains("You are blocked")) {
                ErrorResponse errorResponse = new ErrorResponse(
                        "USER_BLOCKED",
                        message,
                        403);
                return ResponseEntity.status(403).body(errorResponse);
            }

            // For other errors (like invalid credentials)
            ErrorResponse errorResponse = new ErrorResponse(
                    "AUTHENTICATION_FAILED",
                    "Invalid credentials",
                    401);
            return ResponseEntity.status(401).body(errorResponse);
        }
    }

    // ==================== ADMIN ENDPOINTS ====================

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/users/status/{status}")
    public ResponseEntity<List<User>> getUsersByStatus(@PathVariable String status) {
        try {
            User.UserStatus userStatus = User.UserStatus.valueOf(status.toUpperCase());
            return ResponseEntity.ok(userService.getUsersByStatus(userStatus));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/users/{userId}/block")
    public ResponseEntity<Map<String, String>> blockUser(
            @PathVariable Long userId,
            @RequestBody(required = false) Map<String, String> requestBody) {

        String reason = requestBody != null ? requestBody.get("reason") : "Blocked by admin";
        userService.blockUser(userId, reason);

        return ResponseEntity.ok(Map.of(
                "message", "User blocked successfully",
                "userId", userId.toString(),
                "reason", reason));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/users/{userId}/unblock")
    public ResponseEntity<Map<String, String>> unblockUser(@PathVariable Long userId) {
        userService.unblockUser(userId);

        return ResponseEntity.ok(Map.of(
                "message", "User unblocked successfully",
                "userId", userId.toString()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/users/{userId}/status")
    public ResponseEntity<Map<String, Object>> getUserStatus(@PathVariable Long userId) {
        boolean isBlocked = userService.isUserBlocked(userId);
        boolean isActive = userService.isUserActive(userId);
        User.UserStatus status = userService.getUserStatus(userId);
        String blockingReason = isBlocked ? userService.getBlockingReason(userId) : null;

        return ResponseEntity.ok(Map.of(
                "userId", userId,
                "isBlocked", isBlocked,
                "isActive", isActive,
                "status", status.name(),
                "blockingReason", blockingReason != null ? blockingReason : "Not blocked"));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/users/{userId}/status")
    public ResponseEntity<Map<String, String>> setUserStatus(
            @PathVariable Long userId,
            @RequestBody Map<String, String> request) {

        try {
            String statusStr = request.get("status");
            User.UserStatus status = User.UserStatus.valueOf(statusStr.toUpperCase());
            userService.setUserStatus(userId, status);

            return ResponseEntity.ok(Map.of(
                    "message", "User status updated successfully",
                    "userId", userId.toString(),
                    "newStatus", status.name()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Invalid status value"));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/sync-blocked-users")
    public ResponseEntity<Map<String, String>> syncBlockedUsers() {
        userService.syncBlockedUsersWithDatabase();
        return ResponseEntity.ok(Map.of(
                "message", "Blocked users synced with database successfully"));
    }
}