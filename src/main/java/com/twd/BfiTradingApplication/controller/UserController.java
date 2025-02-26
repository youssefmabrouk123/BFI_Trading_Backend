package com.twd.BfiTradingApplication.controller;

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
    public ResponseEntity<User> getAuthenticatedUser() {
        String email = getAuthenticatedUserEmail();
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    @PutMapping("/me")
    public ResponseEntity<User> updateAuthenticatedUser(@RequestBody User user) {
        String email = getAuthenticatedUserEmail();
        return ResponseEntity.ok(userService.updateUserByEmail(email, user));
    }

    @DeleteMapping("/me")
    public ResponseEntity<String> deleteAuthenticatedUser() {
        String email = getAuthenticatedUserEmail();
        userService.deleteUserByEmail(email);
        return ResponseEntity.ok("User deleted successfully");
    }

    private String getAuthenticatedUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName(); // Extract email from token
    }
}
