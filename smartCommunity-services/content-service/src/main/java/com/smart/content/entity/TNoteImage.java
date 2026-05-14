package com.smart.content.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * T_NOTE_IMAGE表实体类（笔记图片）
 */
@Data
@TableName("t_note_image")
public class TNoteImage {

    @Schema(description = "笔记图片ID(主键)")
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @Schema(description = "笔记ID")
    private Long noteId;

    @Schema(description = "图片URL")
    private String url;

    @Schema(description = "图片排序")
    private Integer sortOrder;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "是否删除(0:正常,1:已删除)")
    private Integer isDeleted;
}
