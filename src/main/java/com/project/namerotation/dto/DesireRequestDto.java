package com.project.namerotation.dto;

public class DesireRequestDto {
    private String description;
    private String category;

    // Constructors
    public DesireRequestDto() {}

    public DesireRequestDto(String description, String category) {
        this.description = description;
        this.category = category;
    }

    // Getters and Setters
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}