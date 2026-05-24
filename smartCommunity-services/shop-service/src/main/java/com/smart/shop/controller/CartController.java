package com.smart.shop.controller;

import com.smart.community.common.core.result.Result;
import com.smart.shop.domain.dto.AddProductToCartDTO;
import com.smart.shop.domain.dto.CartProductDTO;
import com.smart.shop.domain.dto.UpdateProductInCartDTO;
import com.smart.shop.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shop")
@RequiredArgsConstructor
@Tag(name = "购物车")
public class CartController {

    private final CartService cartService;


    @PostMapping("/addToCart")
    @Operation(summary = "添加商品到购物车")
    public Result<String> addToCart(@RequestHeader("userId") Long userId,
                              @RequestBody AddProductToCartDTO addProductToCartDTO) {
        cartService.addToCart(userId, addProductToCartDTO);
        return Result.success("添加成功");

    }


    @PutMapping("/updateQuantity")
    @Operation(summary = "更新购物车商品数量")
    public Result<String> updateQuantity(@RequestHeader("userId") Long userId,
                                 @RequestBody UpdateProductInCartDTO updateProductInCartDTO) {

        cartService.updateQuantity(userId, updateProductInCartDTO);
        return Result.success("更新成功");

    }


    @DeleteMapping("/removeProduct")
    @Operation(summary = "删除购物车商品")
    public Result<String> removeProduct(@RequestHeader("userId") Long userId,
                                 @RequestParam Long productId) {
        cartService.removeProduct(userId, productId);
        return Result.success("删除成功");
    }


    @PutMapping("/toggle-check")
    @Operation(summary = "切换购物车商品选中状态")
    public Result<String> toggleCheck(@RequestHeader("userId") Long userId,
                                      @RequestParam Long productId) {

        cartService.toggleCheck(userId, productId);
        return Result.success("切换成功");
    }


    @PutMapping("/toggle-all")
    @Operation(summary = "全选/取消全选购物车商品")
    public Result<String> toggleAll(@RequestHeader("userId") Long userId,
                                    @RequestParam Boolean checkAll) {
        cartService.toggleAllCheck(userId, checkAll);
        return Result.success("操作成功");
    }


    @GetMapping("/getCartList")
    @Operation(summary = "获取购物车列表")
    public Result<List<CartProductDTO>> getCartList(@RequestHeader("userId") Long userId) {
        return Result.success(cartService.getCartList(userId));
    }


    @DeleteMapping("/clearCart")
    @Operation(summary = "清空购物车商品(下单后调用)")
    public Result<String> clearCart(@RequestHeader("userId") Long userId) {
        cartService.clearCart(userId);
        return Result.success("已清空");
    }
}