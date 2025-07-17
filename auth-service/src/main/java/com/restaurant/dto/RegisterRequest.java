package com.restaurant.dto;

import lombok.Data;
import com.restaurant.entity.User.UserRole;

@Data
public class RegisterRequest {
    private String fullname;
    private String password;
    private String email;
    private UserRole role; // optional, defaults to USER
}
