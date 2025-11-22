package com.project.namerotation.controller;

import com.project.namerotation.dto.ApiResponse;
import com.project.namerotation.dto.MonthlyDesireDto;
import com.project.namerotation.dto.MonthlyDesireRequestDto;
import com.project.namerotation.service.MonthlyDesireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/monthly-desires")
@CrossOrigin(origins = "*")
public class MonthlyDesireController {

    private final MonthlyDesireService monthlyDesireService;

    @Autowired
    public MonthlyDesireController(MonthlyDesireService monthlyDesireService) {
        this.monthlyDesireService = monthlyDesireService;
    }

    // Check if user is authenticated
    private boolean isAuthenticated(HttpSession session) {
        return session.getAttribute("userId") != null;
    }

    // Get current monthly desire (Both Admin and User)
    @GetMapping("/current")
    public ResponseEntity<ApiResponse<MonthlyDesireDto>> getCurrentMonthlyDesire(HttpSession session) {
        if (!isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
        }

        try {
            Optional<MonthlyDesireDto> monthlyDesire = monthlyDesireService.getCurrentMonthlyDesire();
            if (monthlyDesire.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success(monthlyDesire.get()));
            } else {
                return ResponseEntity.ok(ApiResponse.success("No monthly desire set for current month", null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to fetch monthly desire: " + e.getMessage()));
        }
    }

    // Create or update monthly desire (Admin)
    @PostMapping
    public ResponseEntity<ApiResponse<MonthlyDesireDto>> createOrUpdateMonthlyDesire(
            @RequestBody MonthlyDesireRequestDto request,
            HttpSession session) {
        
        if (!isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
        }

        try {
            MonthlyDesireDto monthlyDesire = monthlyDesireService.createOrUpdateMonthlyDesire(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Monthly desire updated successfully", monthlyDesire));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to update monthly desire: " + e.getMessage()));
        }
    }

    // Get all monthly desires (Admin)
    @GetMapping
    public ResponseEntity<ApiResponse<List<MonthlyDesireDto>>> getAllMonthlyDesires(HttpSession session) {
        if (!isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
        }

        try {
            List<MonthlyDesireDto> monthlyDesires = monthlyDesireService.getAllMonthlyDesires();
            return ResponseEntity.ok(ApiResponse.success(monthlyDesires));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to fetch monthly desires: " + e.getMessage()));
        }
    }

    // Get monthly desire by ID (Admin)
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MonthlyDesireDto>> getMonthlyDesireById(
            @PathVariable Long id,
            HttpSession session) {
        
        if (!isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
        }

        try {
            Optional<MonthlyDesireDto> monthlyDesire = monthlyDesireService.getMonthlyDesireById(id);
            if (monthlyDesire.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success(monthlyDesire.get()));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Monthly desire not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to fetch monthly desire: " + e.getMessage()));
        }
    }

    // Delete monthly desire (Admin)
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteMonthlyDesire(
            @PathVariable Long id,
            HttpSession session) {
        
        if (!isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
        }

        try {
            monthlyDesireService.deleteMonthlyDesire(id);
            return ResponseEntity.ok(ApiResponse.success("Monthly desire deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to delete monthly desire: " + e.getMessage()));
        }
    }
}