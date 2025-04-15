package com.twd.BfiTradingApplication.controller;

import com.twd.BfiTradingApplication.dto.UserDTO;
import com.twd.BfiTradingApplication.entity.User;
import com.twd.BfiTradingApplication.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<?> getAuthenticatedUser() {
        String email = getAuthenticatedUserEmail();
        if (email == null) {
            return ResponseEntity.status(401).body("User not authenticated");
        }
        try {
            User user = userService.getUserByEmail(email);
            // Convert to DTO to avoid Jackson serialization issues
            UserDTO userDTO = UserDTO.fromUser(user);
            return ResponseEntity.ok(userDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body("User not found");
        }
    }

    @PutMapping("/me")
    public ResponseEntity<?> updateAuthenticatedUser(@RequestBody User updatedUser) {
        String email = getAuthenticatedUserEmail();
        if (email == null) {
            return ResponseEntity.status(401).body("User not authenticated");
        }
        try {
            User user = userService.updateUserByEmail(email, updatedUser);
            // Convert to DTO to avoid Jackson serialization issues
            UserDTO userDTO = UserDTO.fromUser(user);
            return ResponseEntity.ok(userDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/me")
    public ResponseEntity<?> deleteAuthenticatedUser() {
        String email = getAuthenticatedUserEmail();
        if (email == null) {
            return ResponseEntity.status(401).body("User not authenticated");
        }
        try {
            userService.deleteUserByEmail(email);
            return ResponseEntity.ok("User deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    private String getAuthenticatedUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }
        return authentication.getName();
    }
}