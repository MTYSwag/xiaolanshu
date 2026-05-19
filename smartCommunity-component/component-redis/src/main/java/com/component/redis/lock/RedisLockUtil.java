package com.component.redis.lock;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;
import java.util.Collections;
import java.util.UUID;

/**
 * Redis 锁工具类
 */
@Component
public class RedisLockUtil {

    private final StringRedisTemplate redisTemplate;
    private final String lockPrefix = "lock:";

    public RedisLockUtil(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 尝试获取锁
     * @param lockKey 锁名称
     * @param expireSeconds 过期秒数
     * @return 锁的唯一标识（用于释放），null 表示未获取
     */
    public String tryLock(String lockKey, long expireSeconds) {
        String requestId = UUID.randomUUID().toString();
        Boolean success = redisTemplate.opsForValue()
                .setIfAbsent(lockPrefix + lockKey, requestId, expireSeconds, java.util.concurrent.TimeUnit.SECONDS);
        return Boolean.TRUE.equals(success) ? requestId : null;
    }

    /**
     * 释放锁（校验 value）
     */
    public boolean releaseLock(String lockKey, String requestId) {
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(script, Long.class);
        Long result = redisTemplate.execute(redisScript, Collections.singletonList(lockPrefix + lockKey), requestId);
        return result != null && result == 1L;
    }
}