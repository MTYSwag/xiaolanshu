package com.smart.content.remote;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "user-service")  // 对应 Nacos 中的服务名
public interface UserFeignClient {

    @GetMapping("/user/hello")
    String hello();
}