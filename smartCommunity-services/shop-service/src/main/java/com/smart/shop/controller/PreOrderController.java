package com.smart.shop.controller;

import com.smart.community.common.core.result.Result;
import com.smart.shop.domain.vo.PreOrderVO;
import com.smart.shop.service.PreOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/shop/pre-order")
@RequiredArgsConstructor
@Tag(name = "预订单接口")
public class PreOrderController {

    private final PreOrderService preOrderService;

    // 生成预订单
    @PostMapping("/generatePreOrder")
    @Operation(summary = "生成预订单")
    public Result<PreOrderVO> generatePreOrder(@RequestHeader("userId") Long userId) {
        PreOrderVO vo = preOrderService.generatePreOrder(userId);
        return Result.success(vo);
    }


    @GetMapping("/getPreOrderDetail")
    @Operation(summary = "获取预订单详情")
    public Result<PreOrderVO> getPreOrderDetail(@RequestHeader("userId") Long userId,
                                     @RequestParam Long orderId) {
        return Result.success(preOrderService.getPreOrderDetail(orderId, userId));
    }


    @PutMapping("/cancelPreOrder")
    @Operation(summary = "取消预订单")
    public Result<String> cancelPreOrder(@RequestHeader("userId") Long userId,
                                 @RequestParam Long orderId) {
        preOrderService.cancelPreOrder(orderId, userId);
        return Result.success("已取消");

    }
}
