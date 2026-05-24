package com.smart.shop.domain.vo;

import com.smart.shop.entity.PreOrderProduct;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 预订单VO
 */
@Data
public class PreOrderVO {
    @Schema(description = "预订单ID")
    private Long orderId;

    @Schema(description = "预订单总金额")
    private BigDecimal totalAmount;

    @Schema(description = "预订单状态")
    private Integer status;

    @Schema(description = "预订单过期时间")
    private LocalDateTime expireTime;

    @Schema(description = "预订单商品列表")
    private List<PreOrderProduct> preOrderProductList;
}
