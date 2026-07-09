package com.example.aichatroom;

import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.example.aichatroom.entity.User;
import com.example.aichatroom.mapper.Usermapper;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class AichatroomApplicationTests {

    @Resource
    private Usermapper userMapper;

    @Test
    public void testSelect() {
        System.out.println(("----- selectAll method test ------"));
        List<User> userList = userMapper.selectList(null);
        Assert.isTrue(1 == userList.size(), "");
        userList.forEach(System.out::println);
    }
}