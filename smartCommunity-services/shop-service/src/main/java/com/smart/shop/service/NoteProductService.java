package com.smart.shop.service;

import com.smart.shop.entity.Product;

import java.util.List;

public interface NoteProductService {
    /**
     * 笔记关联商品
     * @param noteId 笔记id
     * @param productId 商品id
     */
    void addProductToNote(Long noteId, Long productId);

    /**
     * 笔记取消关联商品
     * @param noteId 笔记id
     * @param productId 商品id
     */
    void removeProductFromNote(Long noteId, Long productId);

    /**
     * 查询笔记关联的商品
     * @param noteId 笔记id
     * @return 商品列表
     */
    List<Product> getProductsByNote(Long noteId);
}
