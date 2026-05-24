package com.smart.search.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 笔记事件DTO
 */
@Data
public class NoteEventDTO {

    @Schema(description = "笔记ID")
    private Long noteId;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "笔记标题")
    private String title;

    @Schema(description = "笔记内容")
    private String content;

    @Schema(description = "图片URL列表")
    private List<String> imageUrls;

    @Schema(description = "话题列表")
    private List<String> topics;

    @Schema(description = "创建时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}