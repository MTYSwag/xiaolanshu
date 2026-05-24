package com.smart.shop.mq;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smart.community.common.core.constants.shop.ShopConstants;
import com.smart.shop.config.RabbitMQConfig;
import com.smart.shop.config.utils.RedisCacheUtil;
import com.smart.shop.domain.dto.rabbitmqMessage.OrderDelayMessage;
import com.smart.shop.entity.Order;
import com.smart.shop.entity.OrderProduct;
import com.smart.shop.mapper.OrderMapper;
import com.smart.shop.mapper.OrderProductMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

/**
 * 订单取消消费者
 * <p>
 * 监听死信队列，处理 30 分钟未支付订单：
 * 1. 幂等检查（仅取消"待支付"状态订单）
 * 2. 更新订单状态为"已取消"
 * 3. 逐商品恢复 Redis 库存
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCancelConsumer {

    private final OrderMapper orderMapper;
    private final OrderProductMapper orderProductMapper;
    private final RedisCacheUtil redisUtil;

    /** 恢复库存 Lua 脚本（INCRBY 原子增加，返回增加后的库存数量） */
    private static final DefaultRedisScript<Long> INCR_STOCK_SCRIPT;
    static {
        INCR_STOCK_SCRIPT = new DefaultRedisScript<>();
        INCR_STOCK_SCRIPT.setLocation(new ClassPathResource("lua/incre_stock.lua"));
        INCR_STOCK_SCRIPT.setResultType(Long.class);
    }

    /**
     * 处理订单取消消息
     * @param message 订单取消消息
     */
    @RabbitListener(queues = RabbitMQConfig.ORDER_CANCEL_QUEUE)
    @Transactional(rollbackFor = Exception.class)
    public void handleOrderCancel(OrderDelayMessage message) {
        if (message == null || message.getOrderId() == null) {
            return;
        }
        log.info("收到订单超时检查消息：orderId={}", message.getOrderId());

        // 1. 查订单
        Order order = orderMapper.selectById(message.getOrderId());
        if (order == null) {
            log.warn("订单不存在：{}", message.getOrderId());
            return;
        }

        // 2. 幂等：仅处理"待支付"状态
        if (order.getStatus() != 1) {
            log.info("订单 {} 已处理（当前状态：{}），跳过", order.getId(), order.getStatus());
            return;
        }

        // 3. 标记已取消
        order.setStatus(3);
        orderMapper.updateById(order);
        log.info("订单 {} 已超时自动取消", order.getId());

        // 4. 查询订单明细，逐商品恢复库存
        List<OrderProduct> items = orderProductMapper.selectList(
                new LambdaQueryWrapper<OrderProduct>()
                        .eq(OrderProduct::getOrderId, order.getId()));
        if (items.isEmpty()) {
            log.warn("订单 {} 无明细记录，跳过库存恢复", order.getId());
            return;
        }
        for (OrderProduct item : items) {
            String stockKey = ShopConstants.STOCK_KEY_PREFIX + item.getProductId();
            Long newStock = redisUtil.execute(INCR_STOCK_SCRIPT,
                    Collections.singletonList(stockKey),
                    String.valueOf(item.getQuantity()));
            log.info("订单 {} 恢复库存：productId={}, 数量={}, 恢复后库存={}",
                    order.getId(), item.getProductId(), item.getQuantity(), newStock);
        }
    }
}