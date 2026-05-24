package com.smart.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
/**
 * T_NOTE_PRODUCT 笔记商品关联表（笔记内嵌商品链接）
 */
@Data
@TableName("t_note_product")
public class NoteProduct {
    @Schema(description = "主键ID")
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @Schema(description = "笔记ID")
    private Long noteId;

    @Schema(description = "商品ID")
    private Long productId;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}