package com.smart.community.gateway.filter;

import com.smart.community.common.core.constants.MyConstants;
import com.smart.community.common.core.result.Result;
import com.smart.community.common.security.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import com.fasterxml.jackson.databind.ObjectMapper;
import cn.hutool.core.util.*;

/**
 * 实现 Redis 黑名单过滤器，用于检查请求中的 JWT Token 是否在黑名单中
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtBlacklistFilter implements GlobalFilter, Ordered {

    private final ReactiveRedisTemplate<String, String> redisTemplate;
    private final JwtUtils jwtUtils;
    private final ObjectMapper objectMapper;

    /**
     * 检查请求中的 JWT Token 是否在黑名单中
     * 如果在黑名单中，拒绝访问
     * 如果不在黑名单中，放行
     * @param exchange the current server exchange
     * @param chain provides a way to delegate to the next filter
     * @return Mono<Void> 表示异步操作，用于处理未授权访问的响应
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 获取请求头中的 Token
        String token = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (StrUtil.isEmpty(token) || !token.startsWith("Bearer ")) {
            // 没有 Token，可能是公开接口，直接放行，交给后续逻辑处理
            return chain.filter(exchange);
        }

        token = token.substring(7); // 去除 "Bearer " 前缀

        // 检查 Token 是否在黑名单中
        String blacklistKey = MyConstants.JWT_BLACKLIST + token;
        return redisTemplate.hasKey(blacklistKey)
                .flatMap(isBlacklisted -> { //flatMap 用于处理异步操作(等上一步执行完，拿到结果，然后做下一步逻辑。)
                    if (Boolean.TRUE.equals(isBlacklisted)) {
                        // 在黑名单中，拒绝访问
                        return unauthorized(exchange, "Token已失效，请重新登录");
                    }
                    // 不在黑名单中，放行
                    return chain.filter(exchange);
                })
                .onErrorResume(e -> {
                    // Redis 异常时，为了系统可用性，可以选择放行或拒绝，这里选择记录日志并放行
                    log.error("Redis黑名单检查异常", e);
                    return chain.filter(exchange);
                });
    }

    /**
     * 处理未授权访问
     * @param exchange 当前服务器交换上下文
     * @param message 错误信息
     * @return Mono<Void> 表示异步操作，用于处理未授权访问的响应
     */
    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        Result<?> result = Result.fail(401, message);
        byte[] bytes;
        try {
            bytes = objectMapper.writeValueAsBytes(result);
        } catch (Exception e) {
            bytes = "{\"code\":401,\"message\":\"未授权\"}".getBytes();
        }
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        // 设置优先级，数字越小越优先，我们希望在鉴权后执行
        return -10;
    }
}
