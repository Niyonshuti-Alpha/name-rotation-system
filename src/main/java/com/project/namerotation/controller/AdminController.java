package com.project.namerotation.controller;

import com.project.namerotation.dto.ApiResponse;
import com.project.namerotation.service.NameService;
import com.project.namerotation.service.TaskService;
import com.project.namerotation.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    private final NameService nameService;
    private final TaskService taskService;
    private final UserService userService;

    @Autowired
    public AdminController(NameService nameService, TaskService taskService, UserService userService) {
        this.nameService = nameService;
        this.taskService = taskService;
        this.userService = userService;
    }

    // Check if user is authenticated
    private boolean isAuthenticated(HttpSession session) {
        return session.getAttribute("userId") != null;
    }

    // Get dashboard statistics
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboard(HttpSession session) {
        if (!isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
        }

        try {
            Map<String, Object> stats = new HashMap<>();
            
            // Get statistics
            stats.put("totalNames", nameService.getAllNames().size());
            stats.put("activeNames", nameService.getActiveNamesCount());
            stats.put("tasksToday", taskService.getAllTasksForToday().size());
            stats.put("normalTasks", taskService.getNormalTasks().size());
            stats.put("specialTasks", taskService.getSpecialTasks().size());
            stats.put("currentDate", LocalDate.now().toString());
            stats.put("latestSessionDate", taskService.getLatestSessionDate());

            return ResponseEntity.ok(ApiResponse.success("Dashboard data retrieved", stats));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to fetch dashboard: " + e.getMessage()));
        }
    }

    // Get system info
    @GetMapping("/info")
    public ResponseEntity<ApiResponse<Map<String, String>>> getSystemInfo(HttpSession session) {
        if (!isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
        }

        try {
            Map<String, String> info = new HashMap<>();
            info.put("appName", "Name Rotation System");
            info.put("version", "1.0.0");
            info.put("currentUser", (String) session.getAttribute("username"));
            info.put("userRole", (String) session.getAttribute("role"));
            info.put("sessionId", session.getId());

            return ResponseEntity.ok(ApiResponse.success("System info retrieved", info));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to fetch system info: " + e.getMessage()));
        }
    }
}