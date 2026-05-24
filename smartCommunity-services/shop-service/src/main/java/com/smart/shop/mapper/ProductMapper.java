package com.smart.shop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smart.shop.entity.Product;
import org.apache.ibatis.annotations.Mapper;
/**
 * 商品Mapper接口 T_Product表
 */

@Mapper
public interface ProductMapper extends BaseMapper<Product> {
}
