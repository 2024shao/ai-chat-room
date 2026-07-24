package com.example.aichatroom.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.aichatroom.entity.RoomMember;

@Mapper
public interface RoomMemberMapper extends BaseMapper<RoomMember> {

}