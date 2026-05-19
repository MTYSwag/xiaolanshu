package com.smart.interact.domain.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 评论请求参数
 * @author mty
 */
@Data
public class CommentReq {

    @Schema(description = "笔记id")
    private Long noteId;

    @Schema(description = "评论内容")
    private String content;
}
