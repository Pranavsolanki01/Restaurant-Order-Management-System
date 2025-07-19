package com.restaurant.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.restaurant.dto.JwtResponse;
import com.restaurant.dto.LoginRequest;
import com.restaurant.dto.RegisterRequest;
import com.restaurant.entity.User;
import com.restaurant.jwt.JwtUtil;

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {

    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final IUserService userService; // Use unified service interface

    @Override
    public JwtResponse register(RegisterRequest request) {
        if (userService.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setFullname(request.getFullname());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setRole(request.getRole() != null ? request.getRole() : User.UserRole.USER);

        userService.saveUser(user);

        String token = jwtUtil.generateToken(user.getFullname(), user.getEmail(), user.getId(), user.getRole().name());
        return new JwtResponse(token, user.getFullname());
    }

    @Override
    public JwtResponse login(LoginRequest request) {
        // First, get the user by email to check blocking status
        User user = userService.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        // 1. Check if user is blocked in Redis cache
        if (userService.isUserBlocked(user.getId())) {
            String reason = userService.getBlockingReason(user.getId());
            throw new RuntimeException("You are blocked: connect to the IT Support team. Reason: " + reason);
        }

        // 2. Check user status in database
        if (user.getStatus() == User.UserStatus.BLOCKED) {
            // If not in Redis but blocked in DB, add to Redis for faster future checks
            userService.blockUser(user.getId(), "Blocked by admin");
            throw new RuntimeException("You are blocked by admin: connect to the IT Support team");
        }
        // 3. If user is not active, make them active
        if (user.getStatus() != User.UserStatus.ACTIVE) {
            user.setStatus(User.UserStatus.ACTIVE);
            userService.saveUser(user);
        }

        // 4. Proceed with normal authentication
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        // 5. Update last login timestamp
        userService.updateLastLogin(user.getId());

        // Generate token using your custom User entity
        String token = jwtUtil.generateToken(user.getFullname(), user.getEmail(), user.getId(), user.getRole().name());

        return new JwtResponse(token, user.getFullname());
    }

}
