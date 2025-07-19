package com.restaurant.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Authentication", description = "Authentication and User Management API")
public class AuthController {
    private final IAuthService authService;
    private final IUserService userService;

    @PostMapping("/register")
    @Operation(summary = "Register new user", description = "Create a new user account in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User registered successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = JwtResponse.class), examples = @ExampleObject(value = "{\"token\":\"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\",\"fullname\":\"John Doe\"}"))),
            @ApiResponse(responseCode = "400", description = "Email already exists", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"error\":\"EMAIL_EXISTS\",\"message\":\"Email already exists\",\"status\":400}")))
    })
    public ResponseEntity<JwtResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user credentials and return JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful", content = @Content(mediaType = "application/json", schema = @Schema(implementation = JwtResponse.class), examples = @ExampleObject(value = "{\"token\":\"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\",\"fullname\":\"John Doe\"}"))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = "{\"error\":\"AUTHENTICATION_FAILED\",\"message\":\"Invalid credentials\",\"status\":401}"))),
            @ApiResponse(responseCode = "403", description = "User is blocked", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(value = "{\"error\":\"USER_BLOCKED\",\"message\":\"You are blocked: connect to the IT Support team. Reason: Blocked by admin\",\"status\":403}")))
    })
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
    @Operation(summary = "Get all users", description = "Retrieve all users in the system (Admin only)", security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
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
    @Operation(summary = "Block user", description = "Block a specific user with an optional reason (Admin only)", security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User blocked successfully", content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"message\":\"User blocked successfully\",\"userId\":\"1\",\"reason\":\"Blocked by admin\"}"))),
            @ApiResponse(responseCode = "403", description = "Access denied - Admin role required"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<Map<String, String>> blockUser(
            @Parameter(description = "User ID to block", required = true) @PathVariable Long userId,
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