package com.project.namerotation.controller;

import com.project.namerotation.dto.ApiResponse;
import com.project.namerotation.dto.DesireDto;
import com.project.namerotation.dto.DesireRequestDto;
import com.project.namerotation.service.DesireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/api/desires")
@CrossOrigin(origins = "*")
public class DesireController {

    private final DesireService desireService;

    @Autowired
    public DesireController(DesireService desireService) {
        this.desireService = desireService;
    }

    // Check if user is authenticated
    private boolean isAuthenticated(HttpSession session) {
        return session.getAttribute("userId") != null;
    }

    // Create desire (Admin)
    @PostMapping
    public ResponseEntity<ApiResponse<DesireDto>> createDesire(
            @RequestBody DesireRequestDto request,
            HttpSession session) {
        
        if (!isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
        }

        try {
            DesireDto desire = desireService.createDesire(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Desire created successfully", desire));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to create desire: " + e.getMessage()));
        }
    }

    // Get all desires (Both Admin and User)
    @GetMapping
    public ResponseEntity<ApiResponse<List<DesireDto>>> getAllDesires(HttpSession session) {
        if (!isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
        }

        try {
            List<DesireDto> desires = desireService.getAllDesires();
            return ResponseEntity.ok(ApiResponse.success(desires));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to fetch desires: " + e.getMessage()));
        }
    }

    // Get desires by category
    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse<List<DesireDto>>> getDesiresByCategory(
            @PathVariable String category,
            HttpSession session) {
        
        if (!isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
        }

        try {
            List<DesireDto> desires = desireService.getDesiresByCategory(category);
            return ResponseEntity.ok(ApiResponse.success(desires));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to fetch desires: " + e.getMessage()));
        }
    }

    // Get short-term desires
    @GetMapping("/short-term")
    public ResponseEntity<ApiResponse<List<DesireDto>>> getShortTermDesires(HttpSession session) {
        if (!isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
        }

        try {
            List<DesireDto> desires = desireService.getShortTermDesires();
            return ResponseEntity.ok(ApiResponse.success(desires));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to fetch short-term desires: " + e.getMessage()));
        }
    }

    // Get long-term desires
    @GetMapping("/long-term")
    public ResponseEntity<ApiResponse<List<DesireDto>>> getLongTermDesires(HttpSession session) {
        if (!isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
        }

        try {
            List<DesireDto> desires = desireService.getLongTermDesires();
            return ResponseEntity.ok(ApiResponse.success(desires));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to fetch long-term desires: " + e.getMessage()));
        }
    }

    // Update desire (Admin)
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DesireDto>> updateDesire(
            @PathVariable Long id,
            @RequestBody DesireRequestDto request,
            HttpSession session) {
        
        if (!isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
        }

        try {
            DesireDto desire = desireService.updateDesire(id, request);
            return ResponseEntity.ok(ApiResponse.success("Desire updated successfully", desire));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to update desire: " + e.getMessage()));
        }
    }

    // Delete desire (Admin)
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteDesire(
            @PathVariable Long id,
            HttpSession session) {
        
        if (!isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
        }

        try {
            desireService.deleteDesire(id);
            return ResponseEntity.ok(ApiResponse.success("Desire deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to delete desire: " + e.getMessage()));
        }
    }
}