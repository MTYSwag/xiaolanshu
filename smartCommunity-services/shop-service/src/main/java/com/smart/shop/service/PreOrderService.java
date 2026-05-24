package com.smart.shop.service;

import com.smart.shop.domain.vo.PreOrderVO;

public interface PreOrderService {
    /**
     * 生成预订单
     * @param userId 用户ID
     * @return 预订单VO
     */
    PreOrderVO generatePreOrder(Long userId);

    /**
     * 获取预订单详情
     * @param orderId 预订单ID
     * @param userId 用户ID
     * @return 预订单VO
     */
    PreOrderVO getPreOrderDetail(Long orderId, Long userId);

    /**
     * 取消预订单
     * @param orderId 预订单ID
     * @param userId 用户ID
     */
    void cancelPreOrder(Long orderId, Long userId);
}
