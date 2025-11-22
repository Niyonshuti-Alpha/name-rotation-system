package com.project.namerotation.dto;

import java.util.List;

public class BulkCreateUserRequest {
    private List<UserDto> accounts;

    // Constructors
    public BulkCreateUserRequest() {
    }

    public BulkCreateUserRequest(List<UserDto> accounts) {
        this.accounts = accounts;
    }

    // Getters and Setters
    public List<UserDto> getAccounts() { 
        return accounts; 
    }
    
    public void setAccounts(List<UserDto> accounts) { 
        this.accounts = accounts; 
    }
}