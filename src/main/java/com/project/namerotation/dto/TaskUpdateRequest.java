package com.project.namerotation.dto;

public class TaskUpdateRequest {
    
    private Long taskId;
    private String taskName;
    private Long newNameId;
    
    // Constructors
    public TaskUpdateRequest() {
    }
    
    public TaskUpdateRequest(Long taskId, String taskName, Long newNameId) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.newNameId = newNameId;
    }
    
    // Getters and Setters
    public Long getTaskId() {
        return taskId;
    }
    
    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }
    
    public String getTaskName() {
        return taskName;
    }
    
    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }
    
    public Long getNewNameId() {
        return newNameId;
    }
    
    public void setNewNameId(Long newNameId) {
        this.newNameId = newNameId;
    }
}
