package com.smart.interact.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * T_COMMENT表实体类（评论）
 */

@Data
@TableName("t_comment")
public class TComment {

    @Schema(description = "主键id")
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @Schema(description = "笔记ID")
    private Long noteId;

    @Schema(description = "评论者ID")
    private Long userId;

    @Schema(description = "评论内容")
    private String content;

    @Schema(description = "0表示一级评论，非0表示回复的评论ID")
    private Long parentId;

    @Schema(description = "被回复的用户ID（仅回复时使用）")
    private Long replyToUserId;

    @Schema(description = "0正常 1已删除")
    private Integer isDeleted;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}