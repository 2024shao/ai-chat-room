package com.example.aichatroom;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@RequestMapping("/index")
public class AichatroomApplication {
    public static void main(String[] args) {
        SpringApplication.run(AichatroomApplication.class, args);
    }

    @GetMapping
    public String index() {
        return "测试Get无参方法请求";
    }
}