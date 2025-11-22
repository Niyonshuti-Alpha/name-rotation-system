package com.project.namerotation.dto;

public class AnnouncementRequestDto {
    private String title;
    private String content;
    private String sendTo;
    private Long specificUserId;

    // Constructors
    public AnnouncementRequestDto() {}

    public AnnouncementRequestDto(String title, String content, String sendTo, Long specificUserId) {
        this.title = title;
        this.content = content;
        this.sendTo = sendTo;
        this.specificUserId = specificUserId;
    }

    // Getters and Setters
    public String getTitle() { 
        return title; 
    }
    
    public void setTitle(String title) { 
        this.title = title; 
    }

    public String getContent() { 
        return content; 
    }
    
    public void setContent(String content) { 
        this.content = content; 
    }

    public String getSendTo() { 
        return sendTo; 
    }
    
    public void setSendTo(String sendTo) { 
        this.sendTo = sendTo; 
    }

    public Long getSpecificUserId() { 
        return specificUserId; 
    }
    
    public void setSpecificUserId(Long specificUserId) { 
        this.specificUserId = specificUserId; 
    }
}