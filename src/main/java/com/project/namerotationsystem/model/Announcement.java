package com.project.namerotationsystem.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "announcements")
public class Announcement {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 200)
    private String title;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    
    @Column(name = "send_to", nullable = false, length = 20)
    private String sendTo; // "ALL" or "SPECIFIC"
    
    @ManyToOne
    @JoinColumn(name = "specific_user_id")
    private User specificUser; // null if sendTo = "ALL"
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "expires_at")
    private LocalDateTime expiresAt; // Auto-delete after 1 week
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        // Set expiresAt to 1 week from creation
        expiresAt = LocalDateTime.now().plusWeeks(1);
    }
    
    // Constructors
    public Announcement() {}
    
    public Announcement(String title, String content, String sendTo) {
        this.title = title;
        this.content = content;
        this.sendTo = sendTo;
    }
    
    public Announcement(String title, String content, String sendTo, User specificUser) {
        this.title = title;
        this.content = content;
        this.sendTo = sendTo;
        this.specificUser = specificUser;
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
    
    public User getSpecificUser() { return specificUser; }
    public void setSpecificUser(User specificUser) { this.specificUser = specificUser; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
}