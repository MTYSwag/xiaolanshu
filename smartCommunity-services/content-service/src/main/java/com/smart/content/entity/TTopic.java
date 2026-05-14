package com.smart.content.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * T_TOPIC表实体类（主题）
 */
@Data
@TableName("t_topic")
public class TTopic {

    @Schema(description = "主题ID(主键)")
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @Schema(description = "主题名称")
    private String name;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
