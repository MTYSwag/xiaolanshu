package com.smart.shop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smart.community.common.core.constants.shop.ShopConstants;
import com.smart.community.common.core.exception.BusinessException;
import com.smart.shop.domain.dto.CartProductDTO;
import com.smart.shop.domain.vo.PreOrderVO;
import com.smart.shop.entity.PreOrder;
import com.smart.shop.entity.PreOrderProduct;
import com.smart.shop.entity.Product;
import com.smart.shop.mapper.PreOrderMapper;
import com.smart.shop.mapper.PreOrderProductMapper;
import com.smart.shop.service.CartService;
import com.smart.shop.service.PreOrderService;
import com.smart.shop.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class PreOrderServiceImpl implements PreOrderService {


    private final CartService cartService;
    private final ProductService productService;
    private final PreOrderMapper preOrderMapper;
    private final PreOrderProductMapper preOrderProductMapper;

    /**
     * 从购物车生成预订单（仅选中商品）
     */
    @Transactional(rollbackFor = Exception.class)
    public PreOrderVO generatePreOrder(Long userId) {
        // 1. 获取购物车中勾选的商品
        List<CartProductDTO> cartItems = cartService.getCartList(userId).stream()
                .filter(CartProductDTO::getChecked)
                .toList();
        if (cartItems.isEmpty()) {
            throw new BusinessException(ShopConstants.CART_PRODUCT_NOT_CHECKED);
        }

        // 2. 库存预检查（模拟，Day33 会优化为 Lua 原子操作）
        for (CartProductDTO cartProductDTO : cartItems) {
            Product product = productService.getProductDetail(cartProductDTO.getProductId());
            if (Objects.isNull(product) || product.getStatus() != 1) {
                log.error("商品 {} 已下架", cartProductDTO.getProductId());
                throw new BusinessException("商品 [" + product.getName() + "] 已下架");
            }
            if (product.getStock() < cartProductDTO.getQuantity()) {
                log.error("商品 {} 库存不足", cartProductDTO.getProductId());
                throw new BusinessException("商品 [" + product.getName() + "] 库存不足");
            }
        }

        // 3. 计算总金额
        BigDecimal totalAmount = cartItems.stream()
                .map(item -> item.getPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 4. 创建预订单
        PreOrder preOrder = new PreOrder();
        preOrder.setUserId(userId);
        preOrder.setTotalAmount(totalAmount);
        preOrder.setStatus(0); // 待支付
        preOrder.setExpireTime(LocalDateTime.now().plusMinutes(30)); // 30分钟过期
        preOrderMapper.insert(preOrder);

        // 5. 创建订单明细（商品快照）
        for (CartProductDTO item : cartItems) {
            PreOrderProduct orderItem = new PreOrderProduct();
            orderItem.setPreOrderId(preOrder.getId());
            orderItem.setProductId(item.getProductId());
            orderItem.setProductName(item.getName());
            orderItem.setPrice(item.getPrice());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setImageUrl(item.getImageUrl());
            preOrderProductMapper.insert(orderItem);
        }

        // 6. 清空购物车中已下单的商品
        for (CartProductDTO item : cartItems) {
            cartService.removeProduct(userId, item.getProductId());
        }

        // 7. 返回预订单信息
        PreOrderVO vo = new PreOrderVO();
        vo.setOrderId(preOrder.getId());
        vo.setTotalAmount(totalAmount);
        vo.setStatus(0);
        vo.setExpireTime(preOrder.getExpireTime());
        vo.setPreOrderProductList(preOrderProductMapper.selectList(
                new LambdaQueryWrapper<PreOrderProduct>().eq(PreOrderProduct::getPreOrderId, preOrder.getId())));
        return vo;
    }

    /**
     * 查询预订单详情
     */
    public PreOrderVO getPreOrderDetail(Long orderId, Long userId) {
        PreOrder preOrder = preOrderMapper.selectById(orderId);
        if (preOrder == null || !preOrder.getUserId().equals(userId)) {
            throw new BusinessException(ShopConstants.PRE_ORDER_NOT_EXIST);
        }
        PreOrderVO vo = new PreOrderVO();
        vo.setOrderId(preOrder.getId());
        vo.setTotalAmount(preOrder.getTotalAmount());
        vo.setStatus(preOrder.getStatus());
        vo.setExpireTime(preOrder.getExpireTime());
        vo.setPreOrderProductList(preOrderProductMapper.selectList(
                new LambdaQueryWrapper<PreOrderProduct>().eq(PreOrderProduct::getPreOrderId, orderId)));
        return vo;
    }

    /**
     * 取消预订单（用户主动取消）
     */
    public void cancelPreOrder(Long orderId, Long userId) {
        PreOrder preOrder = preOrderMapper.selectById(orderId);
        if (preOrder == null || !preOrder.getUserId().equals(userId)) {
            throw new BusinessException(ShopConstants.PRE_ORDER_NOT_EXIST);
        }
        if (preOrder.getStatus() != 0) {
            throw new BusinessException(ShopConstants.PRE_ORDER_STATUS_NOT_ALLOW_CANCEL);
        }
        preOrder.setStatus(2); // 已取消
        preOrderMapper.updateById(preOrder);
        // 释放冻结库存（Day33 实现）
    }
}
