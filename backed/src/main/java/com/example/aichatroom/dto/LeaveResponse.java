package com.example.aichatroom.dto;

public class LeaveResponse {
    private boolean transferred;
    private Long newOwnerId;
    private String newOwnerName;

    public LeaveResponse() {
    }

    public LeaveResponse(boolean transferred, Long newOwnerId, String newOwnerName) {
        this.transferred = transferred;
        this.newOwnerId = newOwnerId;
        this.newOwnerName = newOwnerName;
    }

    public boolean isTransferred() {
        return transferred;
    }

    public void setTransferred(boolean transferred) {
        this.transferred = transferred;
    }

    public Long getNewOwnerId() {
        return newOwnerId;
    }

    public void setNewOwnerId(Long newOwnerId) {
        this.newOwnerId = newOwnerId;
    }

    public String getNewOwnerName() {
        return newOwnerName;
    }

    public void setNewOwnerName(String newOwnerName) {
        this.newOwnerName = newOwnerName;
    }
}