package com.project.namerotation.dto;

public class MonthlyDesireRequestDto {
    private String message;

    // Constructors
    public MonthlyDesireRequestDto() {}

    public MonthlyDesireRequestDto(String message) {
        this.message = message;
    }

    // Getters and Setters
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}