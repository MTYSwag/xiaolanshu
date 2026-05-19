package com.smart.file.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {

    /**
     * MinIO 服务端点
     */
    @Value("${minio.endpoint}")
    private String endpoint;

    /**
     * MinIO 访问密钥
     */
    @Value("${minio.access-key}")
    private String accessKey;

    /**
     * MinIO 密钥
     */
    @Value("${minio.secret-key}")
    private String secretKey;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }
}