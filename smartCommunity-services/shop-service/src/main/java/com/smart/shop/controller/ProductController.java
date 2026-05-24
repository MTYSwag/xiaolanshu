package com.smart.shop.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smart.community.common.core.result.Result;
import com.smart.shop.domain.req.CreateProductReq;
import com.smart.shop.domain.req.UpdateProductReq;
import com.smart.shop.domain.vo.ProductVo;
import com.smart.shop.entity.Product;
import com.smart.shop.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shop")
@RequiredArgsConstructor
@Tag(name = "商品模块")
public class ProductController {

    private final ProductService productService;

    @PostMapping("/createProduct")
    @Operation(summary = "创建商品")
    public Result<ProductVo> createProduct(@RequestBody CreateProductReq req,
                                  @RequestHeader("userId") Long userId) {
        req.setShopId(userId);  // 目前shopId=博主ID
        return Result.success(productService.createProduct(req));
    }

    @PutMapping("/updateProduct")
    @Operation(summary = "更新商品")
    public Result<ProductVo> updateProduct(@RequestBody UpdateProductReq req,
                                           @RequestHeader("userId") Long userId) {
        req.setShopId(userId);
        return Result.success(productService.updateProduct(req));
    }

    @PutMapping("/upOrDownProduct/{productId}")
    @Operation(summary = "上下架商品")
    public Result<String> toggleStatus(@PathVariable Long productId,
                                       @RequestHeader("userId") Long userId) {
        productService.upOrDownProduct(productId, userId);
        return Result.success();
    }

    @GetMapping("/getProductDetail")
    @Operation(summary = "商品详情")
    public Result<ProductVo> getProductDetail(@RequestParam Long productId) {
        return Result.success(productService.getProductDetail(productId));
    }

    // 商品列表（可指定shopId）
    @GetMapping("/getProducts")
    @Operation(summary = "商品列表")
    public Result<Page<Product>> getProducts(@RequestParam(required = false) Long shopId,
                                             @RequestParam(defaultValue = "1") int page,
                                             @RequestParam(defaultValue = "10") int size) {
        return Result.success(productService.getProducts(shopId, page, size));
    }

    // 热门商品
    @GetMapping("/getHotProducts")
    @Operation(summary = "热门商品")
    public Result<List<Product>> getHotProducts(@RequestParam(defaultValue = "10") int topN) {
        return Result.success(productService.getHotProducts(topN));
    }
}