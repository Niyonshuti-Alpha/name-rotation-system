package com.project.namerotation.controller;

import com.project.namerotation.dto.ApiResponse;
import com.project.namerotation.dto.AnnouncementDto;
import com.project.namerotation.dto.AnnouncementRequestDto;
import com.project.namerotation.service.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/api/announcements")
@CrossOrigin(origins = "*")
public class AnnouncementController {

    private final AnnouncementService announcementService;

    @Autowired
    public AnnouncementController(AnnouncementService announcementService) {
        this.announcementService = announcementService;
    }

    // Check if user is authenticated
    private boolean isAuthenticated(HttpSession session) {
        return session.getAttribute("userId") != null;
    }

    // Create announcement (Admin)
    @PostMapping
    public ResponseEntity<ApiResponse<AnnouncementDto>> createAnnouncement(
            @RequestBody AnnouncementRequestDto request,
            HttpSession session) {
        
        if (!isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
        }

        try {
            AnnouncementDto announcement = announcementService.createAnnouncement(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Announcement created successfully", announcement));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to create announcement: " + e.getMessage()));
        }
    }

    // Get all announcements (Admin)
    @GetMapping
    public ResponseEntity<ApiResponse<List<AnnouncementDto>>> getAllAnnouncements(HttpSession session) {
        if (!isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
        }

        try {
            List<AnnouncementDto> announcements = announcementService.getAllAnnouncements();
            return ResponseEntity.ok(ApiResponse.success(announcements));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to fetch announcements: " + e.getMessage()));
        }
    }

    // Get user's announcements - FIXED: Uses the correct service method
    @GetMapping("/my-announcements")
    public ResponseEntity<ApiResponse<List<AnnouncementDto>>> getUserAnnouncements(HttpSession session) {
        if (!isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
        }

        try {
            Long userId = (Long) session.getAttribute("userId");
            List<AnnouncementDto> announcements = announcementService.getUserAnnouncements(userId);
            return ResponseEntity.ok(ApiResponse.success(announcements));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to fetch your announcements: " + e.getMessage()));
        }
    }

    // Get user's active announcements (non-expired)
    @GetMapping("/my-announcements/active")
    public ResponseEntity<ApiResponse<List<AnnouncementDto>>> getActiveUserAnnouncements(HttpSession session) {
        if (!isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
        }

        try {
            Long userId = (Long) session.getAttribute("userId");
            List<AnnouncementDto> announcements = announcementService.getActiveUserAnnouncements(userId);
            return ResponseEntity.ok(ApiResponse.success(announcements));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to fetch your active announcements: " + e.getMessage()));
        }
    }

    // Delete announcement (Admin)
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteAnnouncement(
            @PathVariable Long id,
            HttpSession session) {
        
        if (!isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
        }

        try {
            announcementService.deleteAnnouncement(id);
            return ResponseEntity.ok(ApiResponse.success("Announcement deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to delete announcement: " + e.getMessage()));
        }
    }

    // Get active announcements count (Admin)
    @GetMapping("/active/count")
    public ResponseEntity<ApiResponse<Long>> getActiveAnnouncementsCount(HttpSession session) {
        if (!isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
        }

        try {
            long count = announcementService.getActiveAnnouncementsCount();
            return ResponseEntity.ok(ApiResponse.success(count));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to get active announcements count: " + e.getMessage()));
        }
    }
}