package com.smart.interact.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * T_FAVORITE表实体类（收藏表）
 */
@Data
@TableName("t_favorite")
public class TFavorite {

    @Schema(description = "主键id")
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @Schema(description = "用户id")
    private Long userId;

    @Schema(description = "笔记id")
    private Long noteId;

    @Schema(description = "1收藏 0取消收藏")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "修改时间")
    private LocalDateTime updateTime;
}