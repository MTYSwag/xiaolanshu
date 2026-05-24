package com.smart.shop.entity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 正式订单表
 */


@TableName("t_order")
@Data
public class Order {
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "订单ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "预订单ID")
    private Long preOrderId;

    @Schema(description = "订单总金额")
    private BigDecimal totalAmount;

    @Schema(description = "订单状态(1待支付 2已支付 3已取消)")
    private Integer status;

    @Schema(description = "订单创建时间")
    private LocalDateTime createTime;

    @Schema(description = "订单更新时间")
    private LocalDateTime updateTime;


}
