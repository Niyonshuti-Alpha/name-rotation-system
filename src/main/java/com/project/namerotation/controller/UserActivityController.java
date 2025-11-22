package com.project.namerotation.controller;

import com.project.namerotation.dto.ApiResponse;
import com.project.namerotation.dto.UserActivityDto;
import com.project.namerotation.service.UserActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/api/user-activity")
@CrossOrigin(origins = "*")
public class UserActivityController {

    private final UserActivityService userActivityService;

    @Autowired
    public UserActivityController(UserActivityService userActivityService) {
        this.userActivityService = userActivityService;
    }

    // Check if user is authenticated
    private boolean isAuthenticated(HttpSession session) {
        return session.getAttribute("userId") != null;
    }

    // Record user visit (called on page load)
    @PostMapping("/record-visit")
    public ResponseEntity<ApiResponse<String>> recordUserVisit(HttpSession session) {
        if (!isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
        }

        try {
            Long userId = (Long) session.getAttribute("userId");
            userActivityService.recordUserVisit(userId);
            return ResponseEntity.ok(ApiResponse.success("Visit recorded", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to record visit: " + e.getMessage()));
        }
    }

    // Get all user activities (Admin)
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserActivityDto>>> getAllUserActivities(HttpSession session) {
        if (!isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
        }

        try {
            List<UserActivityDto> activities = userActivityService.getAllUserActivities();
            return ResponseEntity.ok(ApiResponse.success(activities));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to fetch user activities: " + e.getMessage()));
        }
    }

    // Get inactive users (Admin)
    @GetMapping("/inactive")
    public ResponseEntity<ApiResponse<List<UserActivityDto>>> getInactiveUsers(
            @RequestParam(defaultValue = "7") int days,
            HttpSession session) {
        
        if (!isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
        }

        try {
            List<UserActivityDto> inactiveUsers = userActivityService.getInactiveUsers(days);
            return ResponseEntity.ok(ApiResponse.success(inactiveUsers));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to fetch inactive users: " + e.getMessage()));
        }
    }

    // Get never visited users (Admin)
    @GetMapping("/never-visited")
    public ResponseEntity<ApiResponse<List<UserActivityDto>>> getNeverVisitedUsers(HttpSession session) {
        if (!isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
        }

        try {
            List<UserActivityDto> neverVisitedUsers = userActivityService.getNeverVisitedUsers();
            return ResponseEntity.ok(ApiResponse.success(neverVisitedUsers));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to fetch never visited users: " + e.getMessage()));
        }
    }

    // Get top visitors (Admin)
    @GetMapping("/top-visitors")
    public ResponseEntity<ApiResponse<List<UserActivityDto>>> getTopVisitors(HttpSession session) {
        if (!isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
        }

        try {
            List<UserActivityDto> topVisitors = userActivityService.getTopVisitors();
            return ResponseEntity.ok(ApiResponse.success(topVisitors));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to fetch top visitors: " + e.getMessage()));
        }
    }

    // Get visit statistics (Admin)
    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<UserActivityService.UserActivityStats>> getVisitStatistics(HttpSession session) {
        if (!isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
        }

        try {
            UserActivityService.UserActivityStats stats = userActivityService.getVisitStatistics();
            return ResponseEntity.ok(ApiResponse.success(stats));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to fetch visit statistics: " + e.getMessage()));
        }
    }
}