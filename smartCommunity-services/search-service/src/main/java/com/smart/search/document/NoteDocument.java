package com.smart.search.document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 笔记文档 — 对应 ES 索引 notes
 * suggest 字段由 ElasticsearchClient 原生 API 管理，不在此类中定义
 * ignoreUnknown = true 防止 _source 中的 suggest 字段导致反序列化失败
 */
@Data
@Document(indexName = "notes")
@JsonIgnoreProperties(ignoreUnknown = true)
public class NoteDocument {
    @Id
    @Schema(description = "笔记ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "笔记标题")
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String title;

    @Schema(description = "笔记内容")
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String content;

    @Schema(description = "话题列表")
    @Field(type = FieldType.Keyword)
    private List<String> topics;

    @Schema(description = "点赞数")
    private Integer likeCount;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}