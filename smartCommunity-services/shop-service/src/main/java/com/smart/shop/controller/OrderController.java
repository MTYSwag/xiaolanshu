package com.smart.shop.controller;

import com.smart.community.common.core.result.Result;
import com.smart.shop.domain.dto.CreateOrderDTO;
import com.smart.shop.entity.Order;
import com.smart.shop.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/shop")
@Tag(name = "正式订单")
@Slf4j
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/generateSubmitToken")
    @Operation(summary = "生成提交token")
    public Result<String> generateSubmitToken(@RequestHeader("userId") Long userId) {
        return Result.success(orderService.generateSubmitToken(userId));
    }


    @PostMapping("/createOrder")
    @Operation(summary = "创建订单(正式下单)")
    public Result<Order> createOrder(@RequestHeader("userId") Long userId,
                                     @RequestBody CreateOrderDTO createOrderDTO) {
        Order order = orderService.createOrder(userId, createOrderDTO);
        return Result.success(order);
    }

}
