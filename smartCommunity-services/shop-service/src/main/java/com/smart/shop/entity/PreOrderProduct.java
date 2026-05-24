package com.smart.shop.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.IdType;
@Data
@TableName("t_pre_order_product")
public class PreOrderProduct {
    @Schema(description = "预订单商品ID")
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @Schema(description = "预订单ID")
    private Long preOrderId;

    @Schema(description = "商品ID")
    private Long productId;

    @Schema(description = "商品名称")
    private String productName;

    @Schema(description = "商品单价")
    private BigDecimal price;

    @Schema(description = "商品数量")
    private Integer quantity;

    @Schema(description = "商品图片URL")
    private String imageUrl;

    @Schema(description = "预订单商品创建时间")
    private LocalDateTime createTime;
}