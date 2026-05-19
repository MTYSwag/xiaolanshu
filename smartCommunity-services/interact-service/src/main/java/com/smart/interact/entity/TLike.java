package com.smart.interact.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_like")
public class TLike {

    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "点赞ID")
    private Long id;

    @Schema(description = "点赞用户ID")
    private Long userId;

    @Schema(description = "点赞笔记ID")
    private Long contentId;

    @Schema(description = "点赞状态(1 点赞 0 取消点赞)")
    private Integer status;

    @Schema(description = "点赞时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

}
