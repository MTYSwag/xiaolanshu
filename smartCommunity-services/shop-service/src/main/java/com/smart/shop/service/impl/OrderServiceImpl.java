package com.smart.shop.service.impl;

import cn.hutool.core.lang.UUID;
import com.smart.community.common.core.constants.shop.ShopConstants;
import com.smart.community.common.core.exception.BusinessException;
import com.smart.shop.config.RabbitMQConfig;
import com.smart.shop.config.utils.RedisCacheUtil;
import com.smart.shop.domain.dto.CartProductDTO;
import com.smart.shop.domain.dto.CreateOrderDTO;
import com.smart.shop.domain.dto.rabbitmqMessage.OrderDelayMessage;
import com.smart.shop.domain.vo.PreOrderVO;
import com.smart.shop.entity.Order;
import com.smart.shop.entity.OrderProduct;
import com.smart.shop.entity.PreOrder;
import com.smart.shop.mapper.OrderMapper;
import com.smart.shop.mapper.OrderProductMapper;
import com.smart.shop.mapper.PreOrderMapper;
import com.smart.shop.service.CartService;
import com.smart.shop.service.OrderService;
import com.smart.shop.service.PreOrderService;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 订单服务实现类
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    /** Lua 脚本，从 resources/lua/*.lua 加载 */
    private static final DefaultRedisScript<Long> ORDER_SUBMIT_TOKEN_SCRIPT;
    private static final DefaultRedisScript<Long> STOCK_DEDUCT_SCRIPT;
    private static final DefaultRedisScript<String> STOCK_ROLLBACK_SCRIPT;
    static {
        ORDER_SUBMIT_TOKEN_SCRIPT = new DefaultRedisScript<>();
        ORDER_SUBMIT_TOKEN_SCRIPT.setLocation(new ClassPathResource("lua/submit_token_check.lua"));
        ORDER_SUBMIT_TOKEN_SCRIPT.setResultType(Long.class);

        STOCK_DEDUCT_SCRIPT = new DefaultRedisScript<>();
        STOCK_DEDUCT_SCRIPT.setLocation(new ClassPathResource("lua/stock_deduct.lua"));
        STOCK_DEDUCT_SCRIPT.setResultType(Long.class);

        STOCK_ROLLBACK_SCRIPT = new DefaultRedisScript<>();
        STOCK_ROLLBACK_SCRIPT.setLocation(new ClassPathResource("lua/stock_rollback.lua"));
        STOCK_ROLLBACK_SCRIPT.setResultType(String.class);
    }

    private final RedisCacheUtil redisCacheUtil;
    private final PreOrderService preOrderService;
    private final CartService cartService;
    private final OrderMapper orderMapper;
    private final OrderProductMapper orderProductMapper;
    private final PreOrderMapper preOrderMapper;
    private final RabbitTemplate rabbitTemplate;

    @Override
    public String generateSubmitToken(Long userId) {
        String token = UUID.randomUUID().toString(true);
        String key = ShopConstants.ORDER_SUBMIT_TOKEN_KEY_PREFIX + userId + ":" + token;
        redisCacheUtil.setString(key, "1", 5, TimeUnit.MINUTES);
        return token;
    }

    /**
     * 创建订单
     * 1.先判断正式订单是否带有提交token
     * 2.去redis中查询是否存在该submit——token，存在则校验通过（说明是正式提交订单），否则抛出异常
     * 3.获取预订单的详情
     * @param userId 用户id
     * @param dto 创建订单dto
     * @return 订单实体
     */
    @Override
    @GlobalTransactional(name = "create-order",rollbackFor = Exception.class)
    @Transactional(rollbackFor = Exception.class)
    public Order createOrder(Long userId, CreateOrderDTO dto) {

        if (dto.getSubmitToken() == null) {
            throw new BusinessException(400, ShopConstants.MISSING_SUBMIT_TOKEN);
        }

        // 1. 原子校验并删除防重 token（Lua 保证原子性）
        String tokenKey = ShopConstants.ORDER_SUBMIT_TOKEN_KEY_PREFIX + userId + ":" + dto.getSubmitToken();
        Long tokenResult = redisCacheUtil.execute(ORDER_SUBMIT_TOKEN_SCRIPT,
                Collections.singletonList(tokenKey), ShopConstants.LUA_AGREE_ONE);
        if (tokenResult == null || tokenResult.equals(0L)) {
            throw new BusinessException(400, ShopConstants.DUPLICATE_SUBMIT_ERROR);
        }

        // 2. 获取订单明细（预订单 / 购物车选中项）
        List<CartProductDTO> orderProducts = resolveOrderProducts(userId, dto);

        // 3. 原子扣库存，失败则回滚已扣部分
        deductStock(orderProducts);

        // 4. 计算总金额
        BigDecimal totalAmount = orderProducts.stream()
                .map(i -> i.getPrice().multiply(new BigDecimal(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 5. 创建订单
        Order order = new Order();
        order.setUserId(userId);
        order.setPreOrderId(dto.getPreOrderId());
        order.setTotalAmount(totalAmount);
        order.setStatus(1); // 待支付（延时消息 30 分钟后检查，未支付则自动取消 + 恢复库存）
        orderMapper.insert(order);

        // 6. 创建订单明细
        for (CartProductDTO item : orderProducts) {
            OrderProduct orderItem = new OrderProduct();
            orderItem.setOrderId(order.getId());
            orderItem.setProductId(item.getProductId());
            orderItem.setProductName(item.getName());
            orderItem.setPrice(item.getPrice());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setImageUrl(item.getImageUrl());
            orderProductMapper.insert(orderItem);
        }

        // 7. 清理购物车中已下单商品
        for (CartProductDTO cartProductDTO : orderProducts) {
            cartService.removeProduct(userId, cartProductDTO.getProductId());
        }

        // 8. 关联预订单 → 更新状态
        if (dto.getPreOrderId() != null) {
            PreOrder preOrder = new PreOrder();
            preOrder.setId(dto.getPreOrderId());
            preOrder.setStatus(1);
            preOrderMapper.updateById(preOrder);
        }
        // 9. 发送延时消息（30 分钟后检查支付状态）
        OrderDelayMessage delayMessage = new OrderDelayMessage(order.getId(), userId);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.ORDER_DELAY_EXCHANGE,
                RabbitMQConfig.ORDER_DELAY_ROUTING_KEY,
                delayMessage
        );
        log.info("订单{}已创建，延时消息已发送", order.getId());

        return order;
    }

    // ==================== 私有方法 ====================

    /**
     * 解析订单明细来源：预订单优先，否则从购物车选中项取
     */
    private List<CartProductDTO> resolveOrderProducts(Long userId, CreateOrderDTO dto) {
        if (dto.getPreOrderId() != null && !dto.getPreOrderId().equals(0L)) {
            PreOrderVO preOrderVO = preOrderService.getPreOrderDetail(dto.getPreOrderId(), userId);
            if (preOrderVO.getStatus() != 0) {
                throw new BusinessException(ShopConstants.PRE_ORDER_EXPIRED_OR_PROCESSED);
            }
            return preOrderVO.getPreOrderProductList().stream().map(item -> {
                CartProductDTO cartDTO = new CartProductDTO();
                cartDTO.setProductId(item.getProductId());
                cartDTO.setName(item.getProductName());
                cartDTO.setPrice(item.getPrice());
                cartDTO.setQuantity(item.getQuantity());
                cartDTO.setImageUrl(item.getImageUrl());
                return cartDTO;
            }).collect(Collectors.toList());
        }
        List<CartProductDTO> checkedItems = cartService.getCartList(userId).stream()
                .filter(CartProductDTO::getChecked)
                .collect(Collectors.toList());
        if (checkedItems.isEmpty()) {
            throw new BusinessException(ShopConstants.CART_PRODUCT_NOT_CHECKED);
        }
        return checkedItems;
    }

    /**
     * 原子扣库存：逐商品执行 Lua 扣减，任一失败则回滚全部已扣库存
     */
    private void deductStock(List<CartProductDTO> cartProductDTOList) {
        List<CartProductDTO> deducted = new ArrayList<>();
        try {
            for (CartProductDTO cartProduct : cartProductDTOList) {
                String stockKey = ShopConstants.STOCK_KEY_PREFIX + cartProduct.getProductId();
                Long remain = redisCacheUtil.execute(STOCK_DEDUCT_SCRIPT,
                        Collections.singletonList(stockKey),
                        String.valueOf(cartProduct.getQuantity()));
                if (remain == null || remain < 0) {
                    throw new BusinessException(400, ShopConstants.STOCK_INSUFFICIENT + " [" + cartProduct.getName() + "]");
                }
                deducted.add(cartProduct);
            }
        } catch (Exception e) {
            // 回滚已扣减库存
            log.warn("原子扣库存失败，回滚已扣库存：{}", deducted);
            for (CartProductDTO deductedItem : deducted) {
                String stockKey = ShopConstants.STOCK_KEY_PREFIX + deductedItem.getProductId();
                redisCacheUtil.execute(STOCK_ROLLBACK_SCRIPT,
                        Collections.singletonList(stockKey),
                        String.valueOf(deductedItem.getQuantity()));
            }
            throw e;  // 重新抛出，触发 @Transactional 回滚
        }
    }
}