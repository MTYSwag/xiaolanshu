package com.smart.interact.domain.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 回复请求参数
 * @author mty
 */
@Data
public class ReplyReq {

    @Schema(description = "笔记id")
    private Long noteId;
    @Schema(description = "要回复的评论ID(为空时为一级回复)")
    private Long parentId;
    @Schema(description = "被回复的用户ID")
    private Long replyToUserId;
    @Schema(description = "回复内容")
    private String content;
}
