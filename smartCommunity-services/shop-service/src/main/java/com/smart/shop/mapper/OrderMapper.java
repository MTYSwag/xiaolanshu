package com.smart.shop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smart.shop.entity.Order;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单表Mapper
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {}
