package com.smart.shop.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 购物车里商品DTO
 */
@Data
public class CartProductDTO {
    @Schema(description = "商品ID")
    private Long productId;

    @Schema(description = "商品名称")
    private String name;

    @Schema(description = "商品单价")
    private BigDecimal price;

    @Schema(description = "商品图片URL")
    private String imageUrl;

    @Schema(description = "商品数量")
    private Integer quantity;

    @Schema(description = "是否选中")
    private Boolean checked;

}
