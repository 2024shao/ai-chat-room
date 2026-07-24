package com.example.aichatroom.dto;

import java.time.LocalDateTime;
import java.util.List;

public class RoomResponse {
    private Long roomId;
    private String roomCode;
    private String roomName;
    private Long ownerId;
    private String ownerName;
    private Integer maxMembers;
    private Integer currentMembers;
    private Integer status;
    private LocalDateTime createdAt;
    private List<MemberInfo> members;

    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }
    public String getRoomCode() { return roomCode; }
    public void setRoomCode(String roomCode) { this.roomCode = roomCode; }
    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }
    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }
    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
    public Integer getMaxMembers() { return maxMembers; }
    public void setMaxMembers(Integer maxMembers) { this.maxMembers = maxMembers; }
    public Integer getCurrentMembers() { return currentMembers; }
    public void setCurrentMembers(Integer currentMembers) { this.currentMembers = currentMembers; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public List<MemberInfo> getMembers() { return members; }
    public void setMembers(List<MemberInfo> members) { this.members = members; }

    public static class MemberInfo {
        private Long userId;
        private String username;
        private String avatar;
        private Integer role;

        public MemberInfo() {}
        public MemberInfo(Long userId, String username, String avatar, Integer role) {
            this.userId = userId;
            this.username = username;
            this.avatar = avatar;
            this.role = role;
        }
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getAvatar() { return avatar; }
        public void setAvatar(String avatar) { this.avatar = avatar; }
        public Integer getRole() { return role; }
        public void setRole(Integer role) { this.role = role; }
    }
}