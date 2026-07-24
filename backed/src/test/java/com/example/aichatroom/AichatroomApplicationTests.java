package com.example.aichatroom;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.aichatroom.entity.User;
import com.example.aichatroom.mapper.Usermapper;

import jakarta.annotation.Resource;

@SpringBootTest
public class AichatroomApplicationTests {

    @Resource
    private Usermapper userMapper;

    @Test
    public void testSelect() {
        System.out.println(("----- selectAll method test ------"));
        List<User> userList = userMapper.selectList(null);
        assert userList != null : "查询结果不应为null";
        System.out.println("当前用户数量: " + userList.size());
        userList.forEach(System.out::println);
    }
}