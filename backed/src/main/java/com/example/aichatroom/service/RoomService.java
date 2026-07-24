package com.example.aichatroom.service;

import java.util.List;

import com.example.aichatroom.dto.CreateRoomRequest;
import com.example.aichatroom.dto.LeaveResponse;
import com.example.aichatroom.dto.RoomResponse;

public interface RoomService {
    RoomResponse createRoom(Long userId, CreateRoomRequest request);
    RoomResponse joinRoom(Long userId, String roomCode);
    LeaveResponse leaveRoom(Long userId, String roomCode);
    void disbandRoom(Long userId, String roomCode);
    RoomResponse getRoom(String roomCode);
    List<RoomResponse> getMyRooms(Long userId);
}