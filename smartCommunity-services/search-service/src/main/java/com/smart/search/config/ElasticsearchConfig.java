package com.smart.search.config;

import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * ES Java Client 配置
 * 核心：用 Spring Boot 的 ObjectMapper（含 JavaTimeModule）替代 ES Client 默认的裸 ObjectMapper
 * 否则 LocalDateTime 反序列化直接炸
 */
@Configuration
public class ElasticsearchConfig {

    @Bean
    @Primary
    public JacksonJsonpMapper jacksonJsonpMapper(ObjectMapper springObjectMapper) {
        return new JacksonJsonpMapper(springObjectMapper);
    }
}