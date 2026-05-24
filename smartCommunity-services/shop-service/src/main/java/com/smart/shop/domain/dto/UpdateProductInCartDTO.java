package com.smart.shop.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 更新购物车里的商品DTO
 */
@Data
public class UpdateProductInCartDTO {

    @Schema(description = "商品ID")
    private Long productId;

    @Schema(description = "新数量")
    private Integer quantity;  // 新数量
}
