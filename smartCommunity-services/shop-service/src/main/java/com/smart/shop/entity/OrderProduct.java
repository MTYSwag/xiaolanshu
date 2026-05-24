package com.smart.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单商品关联表
 */

@TableName("t_order_product")
@Data
public class OrderProduct {

    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "订单商品关联ID")
    private Long id;

    @Schema(description = "订单ID")
    private Long orderId;

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

    @Schema(description = "订单商品关联创建时间")
    private LocalDateTime createTime;

}
