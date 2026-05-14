package com.smart.content.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * T_NOTE_TOPIC表实体类（笔记主题关联）
 */

@Data
@TableName("t_note_topic")
public class TNoteTopic {

    @Schema(description = "笔记主题关联ID(主键)")
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @Schema(description = "笔记ID")
    private Long noteId;

    @Schema(description = "主题ID")
    private Long topicId;

}
