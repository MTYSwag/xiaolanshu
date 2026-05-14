package com.smart.content.domain.dto;

/**
 * 笔记草稿内容DTO
 */
import io.swagger.v3.oas.annotations.Parameter;
import lombok.Data;

/**
 * 笔记草稿内容DTO
 */
@Data
public class DraftContentDTO {
    @Parameter(description = "草稿ID")
    private Long draftId;

    @Parameter(description = "笔记标题")
    private String title;

    @Parameter(description = "笔记内容")
    private String content;
}
