package com.smart.shop.service;

import com.smart.shop.domain.dto.AddProductToCartDTO;
import com.smart.shop.domain.dto.CartProductDTO;
import com.smart.shop.domain.dto.UpdateProductInCartDTO;

import java.util.List;

public interface CartService {
    /**
     * 添加商品到购物车
     * @param userId 用户ID
     * @param addProductToCartDTO 添加商品到购物车DTO对象
     */
    void addToCart(Long userId, AddProductToCartDTO addProductToCartDTO);

    /**
     * 更新购物车商品数量
     * @param userId 用户ID
     * @param updateProductInCartDTO 更新购物车商品数量DTO对象
     */
    void updateQuantity(Long userId, UpdateProductInCartDTO updateProductInCartDTO);

    /**
     * 删除购物车商品
     * @param userId 用户ID
     * @param productId 商品ID
     */
    void removeProduct(Long userId, Long productId);

    /**
     * 切换购物车商品选中状态
     * @param userId 用户ID
     * @param productId 商品ID
     */
    void toggleCheck(Long userId, Long productId);

    /**
     * 切换购物车所有商品选中状态
     * @param userId 用户ID
     * @param checkAll 是否全选
     */
    void toggleAllCheck(Long userId, Boolean checkAll);

    /**
     * 获取购物车商品列表
     * @param userId 用户ID
     * @return 购物车商品DTO列表
     */
    List<CartProductDTO> getCartList(Long userId);

    /**
     * 清空购物车
     * @param userId 用户ID
     */
    void clearCart(Long userId);
}
