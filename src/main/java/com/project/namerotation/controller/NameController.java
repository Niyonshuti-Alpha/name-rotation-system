package com.project.namerotation.controller;

import com.project.namerotation.dto.ApiResponse;
import com.project.namerotation.dto.NameDto;
import com.project.namerotation.service.NameService;
import com.project.namerotationsystem.model.Name;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/names")
@CrossOrigin(origins = "*")
public class NameController {

    private final NameService nameService;

    @Autowired
    public NameController(NameService nameService) {
        this.nameService = nameService;
    }

    // Check if user is authenticated (helper method)
    private boolean isAuthenticated(HttpSession session) {
        return session.getAttribute("userId") != null;
    }

    // Get all names
    @GetMapping
    public ResponseEntity<ApiResponse<List<NameDto>>> getAllNames(HttpSession session) {
        if (!isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
        }

        try {
            List<NameDto> names = nameService.getAllNames().stream()
                    .map(name -> new NameDto(name.getId(), name.getName()))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(ApiResponse.success(names));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to fetch names: " + e.getMessage()));
        }
    }

    // Get all active names
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<NameDto>>> getActiveNames(HttpSession session) {
        if (!isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
        }

        try {
            List<NameDto> names = nameService.getAllActiveNames().stream()
                    .map(name -> new NameDto(name.getId(), name.getName()))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(ApiResponse.success(names));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to fetch active names: " + e.getMessage()));
        }
    }

    // Get names without user accounts
    @GetMapping("/without-accounts")
    public ResponseEntity<ApiResponse<List<NameDto>>> getNamesWithoutAccounts(HttpSession session) {
        if (!isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
        }

        try {
            List<NameDto> names = nameService.getNamesWithoutUserAccounts().stream()
                    .map(name -> new NameDto(name.getId(), name.getName()))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(ApiResponse.success(names));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to fetch names without accounts: " + e.getMessage()));
        }
    }

    // Add new name
    @PostMapping
    public ResponseEntity<ApiResponse<NameDto>> addName(@RequestBody NameDto nameDto, HttpSession session) {
        if (!isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
        }

        try {
            Name savedName = nameService.addName(nameDto.getName());
            NameDto result = new NameDto(savedName.getId(), savedName.getName());

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Name added successfully", result));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to add name: " + e.getMessage()));
        }
    }

    // Update name
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<NameDto>> updateName(
            @PathVariable Long id,
            @RequestBody NameDto nameDto,
            HttpSession session) {

        if (!isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
        }

        try {
            Name updatedName = nameService.updateName(id, nameDto.getName());
            NameDto result = new NameDto(updatedName.getId(), updatedName.getName());

            return ResponseEntity.ok(ApiResponse.success("Name updated successfully", result));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to update name: " + e.getMessage()));
        }
    }

    // Delete name (soft delete)
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteName(@PathVariable Long id, HttpSession session) {
        if (!isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
        }

        try {
            nameService.deleteName(id);
            return ResponseEntity.ok(ApiResponse.success("Name deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to delete name: " + e.getMessage()));
        }
    }

    // Get count of active names
    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Long>> getActiveNamesCount(HttpSession session) {
        if (!isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
        }

        try {
            long count = nameService.getActiveNamesCount();
            return ResponseEntity.ok(ApiResponse.success(count));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to get count: " + e.getMessage()));
        }
    }
}