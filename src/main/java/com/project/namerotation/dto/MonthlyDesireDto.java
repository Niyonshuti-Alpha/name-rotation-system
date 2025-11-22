package com.project.namerotation.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class MonthlyDesireDto {
    private Long id;
    private String message;
    private LocalDate monthYear;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public MonthlyDesireDto() {}

    public MonthlyDesireDto(Long id, String message, LocalDate monthYear, 
                           LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.message = message;
        this.monthYear = monthYear;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public LocalDate getMonthYear() { return monthYear; }
    public void setMonthYear(LocalDate monthYear) { this.monthYear = monthYear; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}