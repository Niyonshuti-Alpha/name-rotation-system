package com.project.namerotation.dto;

public class TaskDisplayRequest {
    
    private Integer numberOfNames;
    
    // Constructors
    public TaskDisplayRequest() {
    }
    
    public TaskDisplayRequest(Integer numberOfNames) {
        this.numberOfNames = numberOfNames;
    }
    
    // Getters and Setters
    public Integer getNumberOfNames() {
        return numberOfNames;
    }
    
    public void setNumberOfNames(Integer numberOfNames) {
        this.numberOfNames = numberOfNames;
    }
}