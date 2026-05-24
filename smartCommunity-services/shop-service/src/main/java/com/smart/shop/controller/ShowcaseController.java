package com.smart.shop.controller;

import com.smart.community.common.core.result.Result;
import com.smart.shop.entity.Product;
import com.smart.shop.service.ShowcaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shop")
@RequiredArgsConstructor
@Tag(name = "博主橱窗")
public class ShowcaseController {

    private final ShowcaseService showcaseService;


    @PostMapping("/addShowcase")
    @Operation(summary = "添加橱窗商品")
    public Result<String> addShowcase(@RequestHeader("userId") Long userId,
                                      @RequestParam Long shopId,//博主店铺id（预留）
                                      @RequestParam Long productId,
                                      @RequestParam(defaultValue = "0") Integer sortOrder) {
        showcaseService.addToShowcase(userId, productId, sortOrder);
        return Result.success();
    }

    @DeleteMapping("/removeShowcase")
    @Operation(summary = "移除橱窗商品")
    public Result<String> removeShowcase(@RequestHeader("userId") Long userId,
                                      @RequestParam Long shopId,//博主店铺id（预留）
                                      @RequestParam Long productId) {

        showcaseService.removeShowcase(userId, productId);
        return Result.success();
    }


    @GetMapping("/getShowcaseProducts")
    @Operation(summary = "查看博主橱窗")
    public Result<List<Product>> getShowcaseProducts(@RequestParam Long shopId) {
        return Result.success(showcaseService.getShowcaseProducts(shopId));
    }
}