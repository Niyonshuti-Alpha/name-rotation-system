package com.project.namerotation.dto;

import java.time.LocalDateTime;

public class AnnouncementDto {
    private Long id;
    private String title;
    private String content;
    private String sendTo;
    private String specificUsername;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;

    // Constructors
    public AnnouncementDto() {}

    public AnnouncementDto(Long id, String title, String content, String sendTo, 
                          String specificUsername, LocalDateTime createdAt, LocalDateTime expiresAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.sendTo = sendTo;
        this.specificUsername = specificUsername;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getSendTo() { return sendTo; }
    public void setSendTo(String sendTo) { this.sendTo = sendTo; }

    public String getSpecificUsername() { return specificUsername; }
    public void setSpecificUsername(String specificUsername) { this.specificUsername = specificUsername; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
}