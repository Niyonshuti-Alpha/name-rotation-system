package com.project.namerotation.dto;

public class IdeaRequestDto {
    private String title;
    private String content;

    // Constructors
    public IdeaRequestDto() {}

    public IdeaRequestDto(String title, String content) {
        this.title = title;
        this.content = content;
    }

    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}