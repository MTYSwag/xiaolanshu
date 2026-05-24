package com.smart.shop.domain.dto;

/**
 * 创建预订单DTO
 */

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class CreatPreOrderDTO {

    @Schema(description = "购物车选中的商品ID列表（前端可传或不传）")
    private List<Long> cartItemIds;
}
