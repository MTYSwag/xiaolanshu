package com.smart.search.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.indices.ExistsRequest;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.io.StringReader;

/**
 * ES 索引初始化
 * 1. 如果索引不存在 → 创建并设置包含 completion suggest 的完整 mapping
 * 2. 如果已存在 → 只更新 suggest 字段的 mapping
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class IndexMappingConfig {

    private final ElasticsearchClient elasticsearchClient;

    @PostConstruct
    public void upsertSuggestMapping() {
        try {
            boolean exists = elasticsearchClient.indices()
                    .exists(ExistsRequest.of(e -> e.index("notes")))
                    .value();

            if (!exists) {
                // 索引不存在：创建完整的 mapping（含 suggest completion 字段）
                String createJson = """
                    {
                      "settings": {
                        "number_of_shards": 1,
                        "number_of_replicas": 0
                      },
                      "mappings": {
                        "properties": {
                          "id":          { "type": "long" },
                          "userId":      { "type": "long" },
                          "title":       { "type": "text", "analyzer": "ik_max_word" },
                          "content":     { "type": "text", "analyzer": "ik_max_word" },
                          "topics":      { "type": "keyword" },
                          "likeCount":   { "type": "integer" },
                          "createTime":  { "type": "date", "format": "yyyy-MM-dd HH:mm:ss||yyyy-MM-dd'T'HH:mm:ss||yyyy-MM-dd'T'HH:mm:ss.SSS||strict_date_optional_time" },
                          "suggest": {
                            "type": "completion",
                            "analyzer": "ik_smart",
                            "preserve_separators": true,
                            "preserve_position_increments": true,
                            "max_input_length": 100
                          }
                        }
                      }
                    }
                    """;
                elasticsearchClient.indices()
                        .create(c -> c.index("notes").withJson(new StringReader(createJson)));
                log.info("ES 索引 notes 创建成功（含 suggest completion 映射）");
            } else {
                // 索引已存在：更新 suggest 字段映射 + 修正日期格式（兼容 Jackson LocalDateTime 序列化）
                String mappingJson = """
                    {
                      "properties": {
                        "createTime": {
                          "type": "date",
                          "format": "yyyy-MM-dd HH:mm:ss||yyyy-MM-dd'T'HH:mm:ss||yyyy-MM-dd'T'HH:mm:ss.SSS||strict_date_optional_time"
                        },
                        "suggest": {
                          "type": "completion",
                          "analyzer": "ik_smart",
                          "preserve_separators": true,
                          "preserve_position_increments": true,
                          "max_input_length": 100
                        }
                      }
                    }
                    """;
                elasticsearchClient.indices()
                        .putMapping(p -> p.index("notes").withJson(new StringReader(mappingJson)));
                log.info("notes 索引 suggest completion 字段映射已就绪");
            }
        } catch (Exception e) {
            log.error("初始化 notes 索引 suggest mapping 失败: {}", e.getMessage(), e);
        }
    }
}