package com.smart.shop.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateOrderDTO {

    @Schema(description = "预订单ID(可选，如果是从预订单过来，填预订单ID)")
    private Long preOrderId;

    @Schema(description = "防重token(必须填)")
    @NotNull(message = "防重token不能为空")
    private String submitToken;
}
