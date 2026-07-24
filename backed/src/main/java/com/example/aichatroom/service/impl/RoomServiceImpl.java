package com.example.aichatroom.service.impl;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.aichatroom.dto.CreateRoomRequest;
import com.example.aichatroom.dto.LeaveResponse;
import com.example.aichatroom.dto.RoomResponse;
import com.example.aichatroom.entity.Room;
import com.example.aichatroom.entity.RoomMember;
import com.example.aichatroom.entity.User;
import com.example.aichatroom.mapper.RoomMapper;
import com.example.aichatroom.mapper.RoomMemberMapper;
import com.example.aichatroom.mapper.Usermapper;
import com.example.aichatroom.service.RoomService;

@Service
public class RoomServiceImpl implements RoomService {

    private static final String CODE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    @Autowired
    private RoomMapper roomMapper;

    @Autowired
    private RoomMemberMapper roomMemberMapper;

    @Autowired
    private Usermapper userMapper;

    @Override
    @Transactional
    public RoomResponse createRoom(Long userId, CreateRoomRequest request) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        String roomCode = generateRoomCode();

        Room room = new Room();
        room.setRoomCode(roomCode);
        room.setRoomName(request.getRoomName());
        room.setOwnerId(userId);
        room.setMaxMembers(request.getMaxMembers() != null ? request.getMaxMembers() : 50);
        room.setStatus(1);
        room.setCreatedAt(LocalDateTime.now());
        room.setUpdatedAt(LocalDateTime.now());
        roomMapper.insert(room);

        RoomMember member = new RoomMember();
        member.setRoomCode(roomCode);
        member.setUserId(userId);
        member.setRole(1);
        member.setJoinedAt(LocalDateTime.now());
        roomMemberMapper.insert(member);

        return buildRoomResponse(room);
    }

    @Override
    @Transactional
    public RoomResponse joinRoom(Long userId, String roomCode) {
        Room room = roomMapper.selectOne(new LambdaQueryWrapper<Room>()
                .eq(Room::getRoomCode, roomCode));

        if (room == null || room.getStatus() != 1) {
            throw new RuntimeException("房间不存在或已解散");
        }

        RoomMember existingMember = roomMemberMapper.selectOne(new LambdaQueryWrapper<RoomMember>()
                .eq(RoomMember::getRoomCode, roomCode)
                .eq(RoomMember::getUserId, userId));
        if (existingMember != null) {
            throw new RuntimeException("你已在该房间中");
        }

        Long memberCount = roomMemberMapper.selectCount(new LambdaQueryWrapper<RoomMember>()
                .eq(RoomMember::getRoomCode, roomCode));
        if (memberCount >= room.getMaxMembers()) {
            throw new RuntimeException("房间已满");
        }

        RoomMember member = new RoomMember();
        member.setRoomCode(roomCode);
        member.setUserId(userId);
        member.setRole(0);
        member.setJoinedAt(LocalDateTime.now());
        roomMemberMapper.insert(member);

        return buildRoomResponse(room);
    }

    @Override
    @Transactional
    public LeaveResponse leaveRoom(Long userId, String roomCode) {
        RoomMember member = roomMemberMapper.selectOne(new LambdaQueryWrapper<RoomMember>()
                .eq(RoomMember::getRoomCode, roomCode)
                .eq(RoomMember::getUserId, userId));
        if (member == null) {
            throw new RuntimeException("你不在该房间中");
        }

        Room room = roomMapper.selectOne(new LambdaQueryWrapper<Room>()
                .eq(Room::getRoomCode, roomCode));

        if (member.getRole() == 0) {
            roomMemberMapper.deleteById(member.getId());
            return new LeaveResponse(false, null, null);
        }

        List<RoomMember> members = roomMemberMapper.selectList(new LambdaQueryWrapper<RoomMember>()
                .eq(RoomMember::getRoomCode, roomCode));

        if (members.size() <= 1) {
            room.setStatus(0);
            room.setUpdatedAt(LocalDateTime.now());
            roomMapper.updateById(room);
            roomMemberMapper.deleteById(member.getId());
            return new LeaveResponse(true, null, null);
        }

        List<RoomMember> otherMembers = members.stream()
                .filter(m -> !m.getUserId().equals(userId))
                .collect(Collectors.toList());

        int randomIndex = RANDOM.nextInt(otherMembers.size());
        RoomMember newOwner = otherMembers.get(randomIndex);

        newOwner.setRole(1);
        roomMemberMapper.updateById(newOwner);

        room.setOwnerId(newOwner.getUserId());
        room.setUpdatedAt(LocalDateTime.now());
        roomMapper.updateById(room);

        roomMemberMapper.deleteById(member.getId());

        User newOwnerUser = userMapper.selectById(newOwner.getUserId());
        return new LeaveResponse(true, newOwner.getUserId(), newOwnerUser.getUsername());
    }

    @Override
    @Transactional
    public void disbandRoom(Long userId, String roomCode) {
        Room room = roomMapper.selectOne(new LambdaQueryWrapper<Room>()
                .eq(Room::getRoomCode, roomCode));

        if (room == null) {
            throw new RuntimeException("房间不存在");
        }
        if (!room.getOwnerId().equals(userId)) {
            throw new RuntimeException("只有房主才能解散房间");
        }
        if (room.getStatus() != 1) {
            throw new RuntimeException("房间已解散，无需重复操作");
        }

        room.setStatus(0);
        room.setUpdatedAt(LocalDateTime.now());
        roomMapper.updateById(room);
    }

    @Override
    public RoomResponse getRoom(String roomCode) {
        Room room = roomMapper.selectOne(new LambdaQueryWrapper<Room>()
                .eq(Room::getRoomCode, roomCode));

        if (room == null) {
            throw new RuntimeException("房间不存在");
        }

        return buildRoomResponse(room);
    }

    @Override
    public List<RoomResponse> getMyRooms(Long userId) {
        List<RoomMember> myMembers = roomMemberMapper.selectList(new LambdaQueryWrapper<RoomMember>()
                .eq(RoomMember::getUserId, userId));

        return myMembers.stream().map(member -> {
            Room room = roomMapper.selectOne(new LambdaQueryWrapper<Room>()
                    .eq(Room::getRoomCode, member.getRoomCode()));
            if (room == null) return null;
            return buildRoomResponse(room);
        }).filter(r -> r != null).collect(Collectors.toList());
    }

    private String generateRoomCode() {
        String code;
        do {
            StringBuilder sb = new StringBuilder(6);
            for (int i = 0; i < 6; i++) {
                sb.append(CODE_CHARS.charAt(RANDOM.nextInt(CODE_CHARS.length())));
            }
            code = sb.toString();
        } while (roomMapper.selectCount(new LambdaQueryWrapper<Room>()
                .eq(Room::getRoomCode, code)) > 0);
        return code;
    }

    private RoomResponse buildRoomResponse(Room room) {
        RoomResponse response = new RoomResponse();
        response.setRoomId(room.getId());
        response.setRoomCode(room.getRoomCode());
        response.setRoomName(room.getRoomName());
        response.setOwnerId(room.getOwnerId());
        response.setMaxMembers(room.getMaxMembers());
        response.setStatus(room.getStatus());
        response.setCreatedAt(room.getCreatedAt());

        User owner = userMapper.selectById(room.getOwnerId());
        if (owner != null) {
            response.setOwnerName(owner.getUsername());
        }

        List<RoomMember> members = roomMemberMapper.selectList(new LambdaQueryWrapper<RoomMember>()
                .eq(RoomMember::getRoomCode, room.getRoomCode()));
        response.setCurrentMembers(members.size());

        List<RoomResponse.MemberInfo> memberInfos = members.stream().map(m -> {
            User u = userMapper.selectById(m.getUserId());
            return new RoomResponse.MemberInfo(
                    m.getUserId(),
                    u != null ? u.getUsername() : "未知",
                    u != null ? u.getAvatar() : null,
                    m.getRole()
            );
        }).collect(Collectors.toList());
        response.setMembers(memberInfos);

        return response;
    }
}