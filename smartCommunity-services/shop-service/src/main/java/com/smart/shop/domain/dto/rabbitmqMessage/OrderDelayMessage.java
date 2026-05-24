package com.smart.shop.domain.dto.rabbitmqMessage;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDelayMessage {

    @Schema(description = "订单ID")
    private Long orderId;

    @Schema(description = "用户id")
    private Long userId;
}
