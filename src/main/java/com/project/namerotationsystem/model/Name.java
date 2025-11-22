package com.project.namerotationsystem.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "names")
public class Name {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(name = "last_displayed_date")
    private LocalDate lastDisplayedDate;
    
    @Column(name = "display_count")
    private Integer displayCount = 0;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    // NEW: OneToOne relationship with User (matching your User entity)
    @OneToOne(mappedBy = "name", cascade = CascadeType.ALL, orphanRemoval = true)
    private User user;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    // Constructors
    public Name() {
    }
    
    public Name(String name) {
        this.name = name;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public LocalDate getLastDisplayedDate() {
        return lastDisplayedDate;
    }
    
    public void setLastDisplayedDate(LocalDate lastDisplayedDate) {
        this.lastDisplayedDate = lastDisplayedDate;
    }
    
    public Integer getDisplayCount() {
        return displayCount;
    }
    
    public void setDisplayCount(Integer displayCount) {
        this.displayCount = displayCount;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    // NEW: Getter and Setter for user (OneToOne)
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    // NEW: Helper method to set user
    public void assignUser(User user) {
        this.user = user;
        user.setName(this);
    }
    
    // NEW: Helper method to remove user
    public void removeUser() {
        if (this.user != null) {
            this.user.setName(null);
            this.user = null;
        }
    }
    
    @Override
    public String toString() {
        return "Name{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}