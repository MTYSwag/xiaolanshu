package com.smart.shop.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("t_pre_order")
public class PreOrder {

    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "预订单ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "订单总金额")
    private BigDecimal totalAmount;

    @Schema(description = "订单状态(0待支付 1已支付 2已取消 3已超时)")
    private Integer status;   // 0待支付 1已支付 2已取消 3已超时

    @Schema(description = "订单过期时间")
    private LocalDateTime expireTime;

    @Schema(description = "订单创建时间")
    private LocalDateTime createTime;

    @Schema(description = "订单更新时间")
    private LocalDateTime updateTime;
}