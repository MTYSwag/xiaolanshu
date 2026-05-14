package com.smart.content.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 发布笔记表单DTO
 */
@Data
public class PublishContentDTO {

    @Schema(description = "笔记标题")
    private String title;

    @Schema(description = "笔记内容")
    private String content;

    @Schema(description = "所发布笔记的图片列表")
    private List<String> imageUrls;

    @Schema(description = "笔记所属话题列表")
    private List<String> topics;
}
