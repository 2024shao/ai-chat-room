package com.example.aichatroom.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/")
    public String hello() {
        return "Welcome to AI Chat Room!";
    }

    @GetMapping("/health")
    public String healthCheck() {
        return "OK";
    }
}