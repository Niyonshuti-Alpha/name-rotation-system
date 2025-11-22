package com.project.namerotation.controller;

import com.project.namerotation.dto.ApiResponse;
import com.project.namerotation.dto.IdeaDto;
import com.project.namerotation.dto.IdeaRequestDto;
import com.project.namerotation.service.IdeaService;
import com.project.namerotationsystem.model.Idea;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/api/ideas")
@CrossOrigin(origins = "*")
public class IdeaController {

    private final IdeaService ideaService;

    @Autowired
    public IdeaController(IdeaService ideaService) {
        this.ideaService = ideaService;
    }

    // Check if user is authenticated
    private boolean isAuthenticated(HttpSession session) {
        return session.getAttribute("userId") != null;
    }

    // Check if user is admin
    private boolean isAdmin(HttpSession session) {
        return "admin".equals(session.getAttribute("username"));
    }

    // Submit new idea (User)
    @PostMapping
    public ResponseEntity<ApiResponse<Idea>> submitIdea(
            @RequestBody IdeaRequestDto ideaRequest,
            HttpSession session) {
        
        if (!isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
        }

        try {
            Long userId = (Long) session.getAttribute("userId");
            Idea idea = ideaService.submitIdea(userId, ideaRequest);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Idea submitted successfully", idea));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to submit idea: " + e.getMessage()));
        }
    }

    // Get all ideas (Admin only)
    @GetMapping
    public ResponseEntity<ApiResponse<List<IdeaDto>>> getAllIdeas(HttpSession session) {
        if (!isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
        }
        
        if (!isAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Admin access required"));
        }

        try {
            List<IdeaDto> ideas = ideaService.getAllIdeas();
            return ResponseEntity.ok(ApiResponse.success("Ideas retrieved successfully", ideas));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to fetch ideas: " + e.getMessage()));
        }
    }

    // Get user's ideas
    @GetMapping("/my-ideas")
    public ResponseEntity<ApiResponse<List<IdeaDto>>> getUserIdeas(HttpSession session) {
        if (!isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
        }

        try {
            Long userId = (Long) session.getAttribute("userId");
            List<IdeaDto> ideas = ideaService.getUserIdeas(userId);
            return ResponseEntity.ok(ApiResponse.success("Your ideas retrieved successfully", ideas));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to fetch your ideas: " + e.getMessage()));
        }
    }

    // Mark idea as viewed (Admin only)
    @PutMapping("/{id}/view")
    public ResponseEntity<ApiResponse<IdeaDto>> markAsViewed(
            @PathVariable Long id,
            HttpSession session) {
        
        if (!isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
        }
        
        if (!isAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Admin access required"));
        }

        try {
            IdeaDto idea = ideaService.markAsViewed(id);
            return ResponseEntity.ok(ApiResponse.success("Idea marked as viewed", idea));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to mark idea as viewed: " + e.getMessage()));
        }
    }

    // Respond to idea (Admin only)
    @PutMapping("/{id}/respond")
    public ResponseEntity<ApiResponse<IdeaDto>> respondToIdea(
            @PathVariable Long id,
            @RequestBody String response,
            HttpSession session) {
        
        if (!isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
        }
        
        if (!isAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Admin access required"));
        }

        try {
            IdeaDto idea = ideaService.respondToIdea(id, response);
            return ResponseEntity.ok(ApiResponse.success("Response sent successfully", idea));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to respond to idea: " + e.getMessage()));
        }
    }

    // Get pending ideas count (Admin only)
    @GetMapping("/pending/count")
    public ResponseEntity<ApiResponse<Long>> getPendingIdeasCount(HttpSession session) {
        if (!isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
        }
        
        if (!isAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Admin access required"));
        }

        try {
            long count = ideaService.getPendingIdeasCount();
            return ResponseEntity.ok(ApiResponse.success("Pending count retrieved", count));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to get pending ideas count: " + e.getMessage()));
        }
    }

    // Get latest ideas for dashboard (Admin only)
    @GetMapping("/latest")
    public ResponseEntity<ApiResponse<List<IdeaDto>>> getLatestIdeas(HttpSession session) {
        if (!isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
        }
        
        if (!isAdmin(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Admin access required"));
        }

        try {
            List<IdeaDto> ideas = ideaService.getLatestIdeas();
            return ResponseEntity.ok(ApiResponse.success("Latest ideas retrieved", ideas));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to fetch latest ideas: " + e.getMessage()));
        }
    }
}