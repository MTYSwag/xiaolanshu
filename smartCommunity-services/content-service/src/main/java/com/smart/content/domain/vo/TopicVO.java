package com.smart.content.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 话题VO类（话题列表专用）
 */

@Data
public class TopicVO {
    @Schema(description = "标签ID")
    private Long id;

    @Schema(description = "标签名称")
    private String name;

    @Schema(description = "关联笔记数")
    private Long noteCount;
}
