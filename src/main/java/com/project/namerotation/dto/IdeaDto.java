package com.project.namerotation.dto;

import java.time.LocalDateTime;

public class IdeaDto {
    private Long id;
    private String username;
    private String title;
    private String content;
    private String status;
    private String adminResponse;
    private LocalDateTime createdAt;
    private LocalDateTime viewedAt;
    private LocalDateTime respondedAt;

    // Constructors
    public IdeaDto() {}

    public IdeaDto(Long id, String username, String title, String content, String status, 
                   String adminResponse, LocalDateTime createdAt, LocalDateTime viewedAt, 
                   LocalDateTime respondedAt) {
        this.id = id;
        this.username = username;
        this.title = title;
        this.content = content;
        this.status = status;
        this.adminResponse = adminResponse;
        this.createdAt = createdAt;
        this.viewedAt = viewedAt;
        this.respondedAt = respondedAt;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getAdminResponse() { return adminResponse; }
    public void setAdminResponse(String adminResponse) { this.adminResponse = adminResponse; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getViewedAt() { return viewedAt; }
    public void setViewedAt(LocalDateTime viewedAt) { this.viewedAt = viewedAt; }

    public LocalDateTime getRespondedAt() { return respondedAt; }
    public void setRespondedAt(LocalDateTime respondedAt) { this.respondedAt = respondedAt; }
}