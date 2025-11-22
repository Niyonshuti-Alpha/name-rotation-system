package com.project.namerotation.controller;

import com.project.namerotation.dto.ApiResponse;
import com.project.namerotation.dto.TaskDisplayRequest;
import com.project.namerotation.dto.TaskDto;
import com.project.namerotation.dto.TaskUpdateRequest;
import com.project.namerotation.service.TaskService;
import com.project.namerotationsystem.model.TaskAssignment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*")
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    // Check if user is authenticated
    private boolean isAuthenticated(HttpSession session) {
        return session.getAttribute("userId") != null;
    }

    // Generate tasks for a session
    @PostMapping("/generate")
    public ResponseEntity<ApiResponse<String>> generateTasks(
            @RequestBody TaskDisplayRequest request,
            HttpSession session) {

        if (!isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
        }

        try {
            taskService.generateTasks(request.getNumberOfNames());
            return ResponseEntity.ok(
                    ApiResponse.success("Tasks generated successfully for " + request.getNumberOfNames() + " names", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to generate tasks: " + e.getMessage()));
        }
    }

    // Get normal tasks for today
    @GetMapping("/normal")
    public ResponseEntity<ApiResponse<List<TaskDto>>> getNormalTasks(HttpSession session) {
        if (!isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
        }

        try {
            List<TaskDto> tasks = taskService.getNormalTasks().stream()
                    .map(task -> new TaskDto(
                            task.getId(),
                            task.getName().getId(),
                            task.getName().getName(),
                            task.getTaskName(),
                            task.getIsSpecialTask()
                    ))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(ApiResponse.success(tasks));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to fetch normal tasks: " + e.getMessage()));
        }
    }

    // Get special tasks for today
    @GetMapping("/special")
    public ResponseEntity<ApiResponse<List<TaskDto>>> getSpecialTasks(HttpSession session) {
        if (!isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
        }

        try {
            List<TaskDto> tasks = taskService.getSpecialTasks().stream()
                    .map(task -> new TaskDto(
                            task.getId(),
                            task.getName().getId(),
                            task.getName().getName(),
                            task.getTaskName(),
                            task.getIsSpecialTask()
                    ))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(ApiResponse.success(tasks));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to fetch special tasks: " + e.getMessage()));
        }
    }

    // Get all tasks for today
    @GetMapping
    public ResponseEntity<ApiResponse<List<TaskDto>>> getAllTasks(HttpSession session) {
        if (!isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
        }

        try {
            List<TaskDto> tasks = taskService.getAllTasksForToday().stream()
                    .map(task -> new TaskDto(
                            task.getId(),
                            task.getName().getId(),
                            task.getName().getName(),
                            task.getTaskName(),
                            task.getIsSpecialTask()
                    ))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(ApiResponse.success(tasks));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to fetch tasks: " + e.getMessage()));
        }
    }

    // Update a task
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TaskDto>> updateTask(
            @PathVariable Long id,
            @RequestBody TaskUpdateRequest request,
            HttpSession session) {

        if (!isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
        }

        try {
            TaskAssignment updatedTask = taskService.updateTask(id, request.getTaskName(), request.getNewNameId());
            TaskDto result = new TaskDto(
                    updatedTask.getId(),
                    updatedTask.getName().getId(),
                    updatedTask.getName().getName(),
                    updatedTask.getTaskName(),
                    updatedTask.getIsSpecialTask()
            );

            return ResponseEntity.ok(ApiResponse.success("Task updated successfully", result));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Failed to update task: " + e.getMessage()));
        }
    }

    // Clear all tasks for today
    @DeleteMapping
    public ResponseEntity<ApiResponse<String>> clearTasks(HttpSession session) {
        if (!isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
        }

        try {
            taskService.clearTodayTasks();
            return ResponseEntity.ok(ApiResponse.success("All tasks cleared successfully", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to clear tasks: " + e.getMessage()));
        }
    }

    // Check if tasks exist for today
    @GetMapping("/exists")
    public ResponseEntity<ApiResponse<Boolean>> checkTasksExist(HttpSession session) {
        if (!isAuthenticated(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
        }

        try {
            boolean exists = taskService.hasTasksForToday();
            return ResponseEntity.ok(ApiResponse.success(exists));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to check tasks: " + e.getMessage()));
        }
    }
}