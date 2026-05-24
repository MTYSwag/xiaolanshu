package com.smart.shop.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 添加商品到购物车DTO
 */
@Data
public class AddProductToCartDTO {
    @Schema(description = "商品ID")
    private Long productId;

    @Schema(description = "添加数量，默认1")
    private Integer quantity;  // 添加数量，默认1
}
