package com.smart.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
/**
 * T_SHOWCASE 博主橱窗表（博主推荐的商品）
 */
@Data
@TableName("t_showcase")
public class Showcase {
    @Schema(description = "主键ID")
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @Schema(description = "店铺ID/博主ID")
    private Long shopId;

    @Schema(description = "商品ID")
    private Long productId;

    @Schema(description = "排序顺序")
    private Integer sortOrder;

    @Schema(description = "状态（1展示 0隐藏）")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}