package com.project.namerotation.dto;

public class TaskDto {
    
    private Long id;
    private Long nameId;
    private String name;
    private String taskName;
    private Boolean isSpecialTask;
    
    // Constructors
    public TaskDto() {
    }
    
    public TaskDto(Long id, Long nameId, String name, String taskName, Boolean isSpecialTask) {
        this.id = id;
        this.nameId = nameId;
        this.name = name;
        this.taskName = taskName;
        this.isSpecialTask = isSpecialTask;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getNameId() {
        return nameId;
    }
    
    public void setNameId(Long nameId) {
        this.nameId = nameId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getTaskName() {
        return taskName;
    }
    
    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }
    
    public Boolean getIsSpecialTask() {
        return isSpecialTask;
    }
    
    public void setIsSpecialTask(Boolean isSpecialTask) {
        this.isSpecialTask = isSpecialTask;
    }
}