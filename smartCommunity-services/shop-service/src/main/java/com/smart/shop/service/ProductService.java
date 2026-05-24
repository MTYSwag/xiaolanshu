package com.smart.shop.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smart.shop.domain.req.CreateProductReq;
import com.smart.shop.domain.req.UpdateProductReq;
import com.smart.shop.domain.vo.ProductVo;
import com.smart.shop.entity.Product;

import java.util.List;

public interface ProductService {
    /**
     * 创建商品
     * @param req 创建商品请求
     * @return 创建的商品VO
     */
    ProductVo createProduct(CreateProductReq req);

    /**
     * 更新商品信息
     * @param req 更新商品请求
     * @return 更新后的商品VO
     */
    ProductVo updateProduct(UpdateProductReq req);

    /**
     * 上架或下架商品
     * @param productId 商品ID
     * @param userId 用户ID/博主ID/店铺ID
     */
    void upOrDownProduct(Long productId, Long userId);

    /**
     * 获取商品详情
     * @param productId 商品ID
     * @return 商品详情VO
     */
    ProductVo getProductDetail(Long productId);

    /**
     * 获取商品列表
     * @param shopId 店铺ID/博主ID
     * @param page 页码
     * @param size 每页数量
     * @return 商品列表VO
     */
    Page<Product> getProducts(Long shopId, int page, int size);

    /**
     * 获取热门商品
     * @param topN 热门商品数量
     * @return 热门商品列表
     */
    List<Product> getHotProducts(int topN);
}
