package com.smart.shop.service;

import com.smart.shop.entity.Product;

import java.util.List;

/**
 * 博主橱窗服务
 */
public interface ShowcaseService {
    /**
     * 加入橱窗
     * @param userId 用户id
     * @param productId 商品id
     * @param sortOrder 排序
     */
    void addToShowcase(Long userId, Long productId, Integer sortOrder);

    /**
     * 移除橱窗商品
     * @param userId 用户id
     * @param productId 商品id
     */
    void removeShowcase(Long userId, Long productId);

    /**
     * 查看博主橱窗
     * @param shopId 店铺id
     * @return 商品列表
     */
    List<Product> getShowcaseProducts(Long shopId);
}
