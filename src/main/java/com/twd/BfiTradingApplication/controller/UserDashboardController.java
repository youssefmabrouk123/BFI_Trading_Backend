package com.twd.BfiTradingApplication.controller;

import com.twd.BfiTradingApplication.dto.UserDashboardStats;
import com.twd.BfiTradingApplication.entity.User;
import com.twd.BfiTradingApplication.service.UserDashboardService;
import com.twd.BfiTradingApplication.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
public class UserDashboardController {

    @Autowired
    private UserDashboardService dashboardService;

    @Autowired
    private UserService userService;

    @GetMapping("/me")
    public UserDashboardStats getDashboardStats() {
        Integer userId = getAuthenticatedUserId();
        return dashboardService.getUserStats(userId);
    }

    private Integer getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userService.getUserByEmail(email);
        return user.getId();
    }
}
