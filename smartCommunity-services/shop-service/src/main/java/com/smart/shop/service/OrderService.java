package com.smart.shop.service;

import com.smart.shop.domain.dto.CreateOrderDTO;
import com.smart.shop.entity.Order;

public interface OrderService {
    /**
     * 获取提交token
     * @param userId 用户id
     * @return 提交token
     */
    String generateSubmitToken(Long userId);

    /**
     * 创建订单(正式下单)
     * @param userId 用户id
     * @param createOrderDTO 创建订单dto
     * @return 订单实体
     */
    Order createOrder(Long userId, CreateOrderDTO createOrderDTO);
}
