package com.smart.shop.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis 配置类
 * <p>
 * 注入两套 RedisTemplate，对应两种序列化策略：
 * <ul>
 *   <li><b>redisTemplate</b>（JSON 序列化）—— 缓存 Java 对象（Product、CartProductDTO 等），
 *       Value 使用 GenericJackson2JsonRedisSerializer 并嵌入 @class 类型信息，
 *       反序列化时自动还原为原始类型而非 LinkedHashMap。</li>
 *   <li><b>stringRedisTemplate</b>（纯字符串序列化）—— Lua 脚本执行、库存计数器、防重 token，
 *       Key/Value 均为 StringRedisSerializer，无 JSON 包裹，
 *       保证 Lua 中 tonumber(ARGV) 和字符串比较（==）结果正确。</li>
 * </ul>
 * <p>
 * <b>为什么不能用同一套模板？</b><br>
 * GenericJackson2JsonRedisSerializer 会把 Java String {@code "3"} 序列化为 JSON 字符串 {@code "\"3\""}
 * （带引号），导致 Lua 脚本中 tonumber() 返回 nil、字符串比较失败。
 * 因此需要两条完全独立的序列化链路。
 */
@Configuration
public class RedisConfig {

    /**
     * JSON 序列化 RedisTemplate —— 缓存 Java 对象。
     * <p>
     * 适用场景：Product 详情缓存、Cart Hash、ZSet 热度排名、Set 操作。
     *
     * @param factory      Redis 连接工厂
     * @param objectMapper Spring Boot 自动配置的 ObjectMapper（含 JavaTimeModule 等）
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory, ObjectMapper objectMapper) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // Key / HashKey 统一用 String 序列化，可读性好
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);

        // Value / HashValue 使用 JSON 序列化，复制 Spring Boot 的 ObjectMapper 并开启 DefaultTyping
        // DefaultTyping：JSON 中嵌入 @class 类型信息，反序列化时还原为具体类型（如 CartProductDTO 而非 LinkedHashMap）
        ObjectMapper redisMapper = objectMapper.copy();
        redisMapper.activateDefaultTyping(redisMapper.getPolymorphicTypeValidator(),
                ObjectMapper.DefaultTyping.NON_FINAL);
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer(redisMapper);
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);

        template.afterPropertiesSet();
        return template;
    }

    /**
     * 纯字符串 StringRedisTemplate —— Lua 脚本执行 & 计数器 & 防重 token。
     * <p>
     * Key / Value 均为 StringRedisSerializer，写入 Redis 的是原始字符串（无 JSON 引号包裹）。
     * <p>
     * Spring Boot 默认已自动装配此 Bean（通过 RedisAutoConfiguration），
     * 此处显式声明以明确架构意图。
     *
     * @param factory Redis 连接工厂
     */
    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory factory) {
        return new StringRedisTemplate(factory);
    }
}