package com.smart.shop.service.impl;

import com.smart.community.common.core.constants.shop.ShopConstants;
import com.smart.community.common.core.exception.BusinessException;
import com.smart.shop.config.utils.RedisCacheUtil;
import com.smart.shop.domain.dto.AddProductToCartDTO;
import com.smart.shop.domain.dto.CartProductDTO;
import com.smart.shop.domain.dto.UpdateProductInCartDTO;
import com.smart.shop.entity.Product;
import com.smart.shop.mapper.ProductMapper;
import com.smart.shop.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 购物车服务实现 —— 基于 Redis Hash
 * 数据结构：HSET cart:user:{userId}  {productId}  CartProductDTO(JSON)
 * RedisConfig 已配置 GenericJackson2JsonRedisSerializer + DefaultTyping，
 * 对象存取自动序列化/反序列化，无需手动 JSON 转换。
 */
@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final RedisCacheUtil redisUtil;
    private final ProductMapper productMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addToCart(Long userId, AddProductToCartDTO dto) {
        String cartKey = ShopConstants.CART_KEY_PREFIX + userId;
        String field = String.valueOf(dto.getProductId());
        int quantity = dto.getQuantity() != null ? dto.getQuantity() : 1;

        // 1. 检查购物车是否已有该商品
        redisUtil.hashGet(cartKey, field, CartProductDTO.class).ifPresentOrElse(
                existing -> {
                    // 已存在 → 累加数量
                    existing.setQuantity(existing.getQuantity() + quantity);
                    redisUtil.hashPut(cartKey, field, existing);
                },
                () -> {
                    // 2. 不存在 → 查询商品信息，新建购物车项
                    Product product = productMapper.selectById(dto.getProductId());
                    if (Objects.isNull(product) || !Objects.equals(product.getStatus(), ShopConstants.PRODUCT_STATUS_ON_SHELF)) {
                        throw new BusinessException(ShopConstants.PRODUCT_NOT_EXIST_OR_OFF_SHELF);
                    }
                    CartProductDTO cartProductDTO = new CartProductDTO();
                    cartProductDTO.setProductId(product.getId());
                    cartProductDTO.setName(product.getName());
                    cartProductDTO.setPrice(product.getPrice());
                    cartProductDTO.setImageUrl(product.getImageUrl());
                    cartProductDTO.setQuantity(quantity);
                    cartProductDTO.setChecked(true);
                    redisUtil.hashPut(cartKey, field, cartProductDTO);
                }
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateQuantity(Long userId, UpdateProductInCartDTO dto) {
        String cartKey = ShopConstants.CART_KEY_PREFIX + userId;
        String field = String.valueOf(dto.getProductId());

        // 1. 检查购物车是否已有该商品
        CartProductDTO cartProductDTO = redisUtil.hashGet(cartKey, field, CartProductDTO.class)
                .orElseThrow(() -> new BusinessException(ShopConstants.CART_PRODUCT_NOT_EXIST));
        // 2. 更新商品数量
        // 2.1 新数量为0 → 删除购物车项
        if (dto.getQuantity() <= 0) {
            redisUtil.hashDelete(cartKey, field);
        } else {
            // 2.2 新数量大于0 → 更新购物车项
            cartProductDTO.setQuantity(dto.getQuantity());
            redisUtil.hashPut(cartKey, field, cartProductDTO);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeProduct(Long userId, Long productId) {
        String cartKey = ShopConstants.CART_KEY_PREFIX + userId;
        redisUtil.hashDelete(cartKey, String.valueOf(productId));
    }

    @Override
    public void toggleCheck(Long userId, Long productId) {
        //组成redis key
        String cartKey = ShopConstants.CART_KEY_PREFIX + userId;
        CartProductDTO cartProductDTO = redisUtil.hashGet(cartKey, String.valueOf(productId), CartProductDTO.class)
                .orElseThrow(() -> new BusinessException(ShopConstants.CART_PRODUCT_NOT_EXIST));
        cartProductDTO.setChecked(!cartProductDTO.getChecked());
        redisUtil.hashPut(cartKey, String.valueOf(productId), cartProductDTO);
    }

    @Override
    public void toggleAllCheck(Long userId, Boolean checkAll) {
        String cartKey = ShopConstants.CART_KEY_PREFIX + userId;
        // 1. 遍历购物车所有商品
        // 2. 更新商品选中状态
        Map<String, CartProductDTO> entries = redisUtil.hashEntries(cartKey, CartProductDTO.class);
        for (Map.Entry<String, CartProductDTO> entry : entries.entrySet()) {
            CartProductDTO item = entry.getValue();
            item.setChecked(checkAll);
            redisUtil.hashPut(cartKey, entry.getKey(), item);
        }
    }

    @Override
    public List<CartProductDTO> getCartList(Long userId) {
        //组成redis key
        String cartKey = ShopConstants.CART_KEY_PREFIX + userId;
        // 1. 查询购物车所有商品
        return new ArrayList<>(redisUtil.hashEntries(cartKey, CartProductDTO.class).values());
    }

    @Override
    public void clearCart(Long userId) {
        //组成redis key
        String cartKey = ShopConstants.CART_KEY_PREFIX + userId;
        redisUtil.delete(cartKey);
    }
}