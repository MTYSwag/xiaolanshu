package com.smart.content.controller;

import com.smart.content.remote.UserFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @Autowired
    private UserFeignClient userFeignClient;

    @GetMapping("content/hello")
    public String hello() {
        return "Hello from content-service!";
    }

    // 测试 Feign 调用 user-service
    @GetMapping("/content/call-user")
    public String callUser() {
        String result = userFeignClient.hello();
        return "调用 user-service 返回: " + result;
    }
}