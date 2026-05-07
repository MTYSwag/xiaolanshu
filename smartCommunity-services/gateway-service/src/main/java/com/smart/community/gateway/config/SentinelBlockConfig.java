package com.smart.community.gateway.config;

import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.BlockRequestHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.smart.community.common.core.result.Result;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;


@Configuration
public class SentinelBlockConfig {

    @Bean
    public BlockRequestHandler blockRequestHandler() {
        return (ServerWebExchange exchange, Throwable e) -> {
            Result<?> result = Result.fail(429, "请求过于频繁，请稍后重试");
            return ServerResponse.status(429)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(result);
        };
    }
}