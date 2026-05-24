package com.smart.shop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smart.shop.entity.OrderProduct;
import org.apache.ibatis.annotations.Mapper;
/**
 * 订单商品关联表Mapper
 */
@Mapper
public interface OrderProductMapper extends BaseMapper<OrderProduct> {}
