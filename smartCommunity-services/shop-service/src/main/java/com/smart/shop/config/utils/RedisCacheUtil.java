package com.smart.shop.config.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Redis 缓存工具类
 * 封装两套 RedisTemplate，按序列化需求分流：
 *   JSON 序列化{@link #redisTemplate}）
 *       读写 Java 对象（Product 详情、Cart Hash、ZSet 热度排名、Set 操作等）。
 *
 *   纯字符串序列化{@link #stringRedisTemplate}）
 *       Lua 脚本执行 ({@link #execute})、库存计数器 ({@link #setRawString})、
 *       防重 token ({@link #setString})。
 * 使用约定
 *   缓存 Java 对象（需 JSON 序列化/反序列化）→ 用 {@link #set} / {@link #get} /
 *       {@link #getOrFetch} / Hash 系列方法
 *   存一个会被 Lua 脚本 GET 出来做 tonumber() 或字符串比较的值 → 用
 *       {@link #setRawString}（无 TTL）或 {@link #setString}（带 TTL）
 *   执行 Lua 脚本 → 用 {@link #execute}
 */
@Slf4j
@Component
public class RedisCacheUtil {

    /** JSON 序列化模板，用于缓存 Java 对象 */
    private final RedisTemplate<String, Object> redisTemplate;
    /** 纯字符串模板，用于 Lua 脚本执行、计数器、防重 token */
    private final StringRedisTemplate stringRedisTemplate;

    public RedisCacheUtil(RedisTemplate<String, Object> redisTemplate,
                          StringRedisTemplate stringRedisTemplate) {
        this.redisTemplate = redisTemplate;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    // ========================================================================
    // JSON 序列化 —— 通用缓存读写（Product 详情等 Java 对象）
    // ========================================================================

    /**
     * 读缓存，自动转型
     *
     * @param key  缓存键
     * @param type 目标类型
     * @return Optional，miss 时为空
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(String key, Class<T> type) {
        Object value = redisTemplate.opsForValue().get(key);
        if (Objects.isNull(value)) {
            return Optional.empty();
        }
        if (type.isInstance(value)) {
            return Optional.of((T) value);
        }
        log.warn("缓存类型不匹配: key={}, expect={}, actual={}",
                key, type.getSimpleName(), value.getClass().getSimpleName());
        return Optional.empty();
    }

    /**
     * 写缓存（JSON 序列化，带过期时间）
     */
    public void set(String key, Object value, long ttl, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, ttl, unit);
    }

    /**
     * 删除缓存
     */
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    /**
     * Cache-Aside：先读缓存，miss 则查库并自动回填
     */
    public <T> T getOrFetch(String key, Class<T> type, Supplier<T> dbSupplier, long ttl, TimeUnit unit) {
        Optional<T> cached = get(key, type);
        if (cached.isPresent()) {
            return cached.get();
        }
        T data = dbSupplier.get();
        if (data != null) {
            set(key, data, ttl, unit);
        }
        return data;
    }

    // ========================================================================
    // 纯字符串序列化 —— 库存计数器 & 防重 token（Lua 脚本依赖这些 key）
    // ========================================================================

    /**
     * 写入纯字符串值（无过期时间），用于库存计数器等持久化数值。
     * 使用 StringRedisTemplate，值以原始字节存储（如 100），
     * 确保 Lua 脚本中 tonumber(GET key) 能正常解析。
     */
    public void setRawString(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }

    /**
     * 写入纯字符串值（带过期时间），用于防重 token 等临时值。
     * 使用 StringRedisTemplate，值以原始字节存储（如 1），
     * 确保 Lua 脚本中 GET key == ARGV[1] 字符串比较结果正确。
     */
    public void setString(String key, String value, long ttl, TimeUnit unit) {
        stringRedisTemplate.opsForValue().set(key, value, ttl, unit);
    }

    // ========================================================================
    // ZSet 操作 —— 热度排名
    // ========================================================================

    /**
     * 增加成员分数
     */
    public void incrementScore(String key, Object member, double delta) {
        redisTemplate.opsForZSet().incrementScore(key, String.valueOf(member), delta);
    }

    /**
     * 按分数倒序取 topN
     */
    public Set<ZSetOperations.TypedTuple<Object>> reverseRangeWithScores(String key, long start, long end) {
        return redisTemplate.opsForZSet().reverseRangeWithScores(key, start, end);
    }

    // ========================================================================
    // Set 操作
    // ========================================================================

    /**
     * 判断 Set 中是否包含成员
     */
    public boolean isMember(String key, Object member) {
        Boolean result = redisTemplate.opsForSet().isMember(key, String.valueOf(member));
        return Boolean.TRUE.equals(result);
    }

    /**
     * 向 Set 添加成员
     */
    public void addToSet(String key, Object member) {
        redisTemplate.opsForSet().add(key, String.valueOf(member));
    }

    /**
     * 从 Set 移除成员
     */
    public void removeFromSet(String key, Object member) {
        redisTemplate.opsForSet().remove(key, String.valueOf(member));
    }

    /**
     * 获取 Set 大小
     */
    public long setSize(String key) {
        Long size = redisTemplate.opsForSet().size(key);
        return size == null ? 0 : size;
    }

    // ========================================================================
    // Hash 操作 —— 购物车
    // ========================================================================

    /**
     * 获取 Hash 字段值，自动转型
     */
    @SuppressWarnings("unchecked")
    public <T> Optional<T> hashGet(String key, Object field, Class<T> type) {
        Object value = redisTemplate.opsForHash().get(key, field);
        if (Objects.isNull(value)) {
            return Optional.empty();
        }
        if (type.isInstance(value)) {
            return Optional.of((T) value);
        }
        log.warn("Hash 字段类型不匹配: key={}, field={}, expect={}, actual={}",
                key, field, type.getSimpleName(), value.getClass().getSimpleName());
        return Optional.empty();
    }

    /**
     * 设置 Hash 字段值
     */
    public void hashPut(String key, Object field, Object value) {
        redisTemplate.opsForHash().put(key, field, value);
    }

    /**
     * 设置 Hash 字段值并设置过期时间
     */
    public void hashPut(String key, Object field, Object value, long ttl, TimeUnit unit) {
        redisTemplate.opsForHash().put(key, field, value);
        redisTemplate.expire(key, ttl, unit);
    }

    /**
     * 删除 Hash 字段（一个或多个）
     */
    public void hashDelete(String key, Object... fields) {
        redisTemplate.opsForHash().delete(key, fields);
    }

    /**
     * 获取 Hash 所有字段值，自动转型
     */
    @SuppressWarnings("unchecked")
    public <T> Map<String, T> hashEntries(String key, Class<T> type) {
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
        Map<String, T> result = new LinkedHashMap<>();
        for (Map.Entry<Object, Object> entry : entries.entrySet()) {
            Object value = entry.getValue();
            if (type.isInstance(value)) {
                result.put(String.valueOf(entry.getKey()), (T) value);
            } else {
                log.warn("Hash entries 类型不匹配: key={}, field={}, expect={}, actual={}",
                        key, entry.getKey(), type.getSimpleName(),
                        value != null ? value.getClass().getSimpleName() : "null");
            }
        }
        return result;
    }

    /**
     * 判断 Hash 中是否存在指定字段
     */
    public boolean hashExists(String key, Object field) {
        return redisTemplate.opsForHash().hasKey(key, field);
    }

    /**
     * 获取 Hash 字段数量
     */
    public long hashSize(String key) {
        return redisTemplate.opsForHash().size(key);
    }

    // ========================================================================
    // Lua 脚本执行 —— 使用 StringRedisTemplate 保证 ARGV 为纯字符串
    // ========================================================================

    /**
     * 执行 Redis Lua 脚本
     * @param script Lua 脚本（预编译为 DefaultRedisScript）
     * @param keys   KEYS 列表
     * @param args   ARGV 参数列表，内部会转为 String[] 以保证纯字符串序列化
     * @param <T>    返回值类型（脚本 resultType 决定）
     * @return 脚本执行结果
     */
    @SuppressWarnings("unchecked")
    public <T> T execute(DefaultRedisScript<T> script, List<String> keys, Object... args) {
        String[] stringArgs = new String[args.length];
        for (int i = 0; i < args.length; i++) {
            stringArgs[i] = args[i].toString();
        }
        return (T) stringRedisTemplate.execute(script, keys, (Object[]) stringArgs);
    }
}