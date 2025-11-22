package com.project.namerotation.dto;

import java.time.LocalDateTime;

public class UserActivityDto {
    private Long id;
    private String username;
    private Integer visitCount;
    private LocalDateTime lastVisit;
    private LocalDateTime createdAt;

    // Constructors
    public UserActivityDto() {}

    public UserActivityDto(Long id, String username, Integer visitCount, 
                          LocalDateTime lastVisit, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.visitCount = visitCount;
        this.lastVisit = lastVisit;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public Integer getVisitCount() { return visitCount; }
    public void setVisitCount(Integer visitCount) { this.visitCount = visitCount; }

    public LocalDateTime getLastVisit() { return lastVisit; }
    public void setLastVisit(LocalDateTime lastVisit) { this.lastVisit = lastVisit; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}