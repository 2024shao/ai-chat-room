package com.example.aichatroom.controller;

import com.example.aichatroom.common.Result;
import com.example.aichatroom.dto.CreateRoomRequest;
import com.example.aichatroom.dto.JoinRoomRequest;
import com.example.aichatroom.dto.LeaveResponse;
import com.example.aichatroom.dto.RoomResponse;
import com.example.aichatroom.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

@Tag(name = "房间管理", description = "创建、加入、退出、解散房间")
@RestController
@RequestMapping("/api/room")
public class RoomController {

    @Resource
    private RoomService roomService;

    @Operation(summary = "创建房间")
    @PostMapping("/create")
    public Result<RoomResponse> createRoom(@RequestBody CreateRoomRequest request, HttpServletRequest httpRequest) {
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            RoomResponse response = roomService.createRoom(userId, request);
            return Result.success("房间创建成功", response);
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    @Operation(summary = "加入房间")
    @PostMapping("/join")
    public Result<RoomResponse> joinRoom(@RequestBody JoinRoomRequest request, HttpServletRequest httpRequest) {
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            RoomResponse response = roomService.joinRoom(userId, request.getRoomCode());
            return Result.success("加入成功", response);
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    @Operation(summary = "退出房间")
    @PostMapping("/leave")
    public Result<LeaveResponse> leaveRoom(@RequestBody JoinRoomRequest request, HttpServletRequest httpRequest) {
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            LeaveResponse response = roomService.leaveRoom(userId, request.getRoomCode());

            String message;
            if (!response.isTransferred()) {
                message = "已退出房间";
            } else if (response.getNewOwnerId() == null) {
                message = "已退出房间，房间已解散";
            } else {
                message = "已退出房间，房主已随机转让";
            }
            return Result.success(message, response);
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    @Operation(summary = "解散房间（仅房主）")
    @PostMapping("/disband")
    public Result<Void> disbandRoom(@RequestBody JoinRoomRequest request, HttpServletRequest httpRequest) {
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            roomService.disbandRoom(userId, request.getRoomCode());
            return Result.success("房间已解散", null);
        } catch (RuntimeException e) {
            String message = e.getMessage();
            int code = 400;
            if ("只有房主才能解散房间".equals(message)) {
                code = 403;
            } else if ("房间不存在".equals(message)) {
                code = 404;
            }
            return Result.error(code, message);
        }
    }

    @Operation(summary = "查询房间信息")
    @GetMapping("/{roomCode}")
    public Result<RoomResponse> getRoom(@Parameter(description = "6位房间号") @PathVariable String roomCode) {
        try {
            RoomResponse response = roomService.getRoom(roomCode);
            return Result.success(response);
        } catch (RuntimeException e) {
            return Result.error(404, e.getMessage());
        }
    }
}