package com.smart.user.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @GetMapping("/user/hello")
    public String hello() {
        return "Hello from user-service!";
    }
}