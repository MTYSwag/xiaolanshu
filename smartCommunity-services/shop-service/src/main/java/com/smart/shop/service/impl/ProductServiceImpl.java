package com.smart.shop.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.community.common.core.constants.shop.ShopConstants;
import com.smart.community.common.core.exception.BusinessException;
import com.smart.shop.config.utils.RedisCacheUtil;
import com.smart.shop.domain.req.CreateProductReq;
import com.smart.shop.domain.req.UpdateProductReq;
import com.smart.shop.domain.vo.ProductVo;
import com.smart.shop.entity.Product;
import com.smart.shop.mapper.ProductMapper;
import com.smart.shop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    private final RedisCacheUtil redisUtil;

    /**
     * 创建商品
     * @param req 创建商品请求
     * @return 创建的商品VO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProductVo createProduct(CreateProductReq req) {
        Product product = new Product();
        BeanUtil.copyProperties(req, product);
        //重复商品名则抛出异常
        if (getOne(new LambdaQueryWrapper<Product>()
                .eq(Product::getName, product.getName())
                .eq(Product::getShopId, product.getShopId())) != null) {
            throw new BusinessException(ShopConstants.PRODUCT_NAME_EXIST);
        }
        //保存商品
        save(product);
        // 同步初始库存到 Redis（Lua 扣库存/恢复库存脚本依赖此 key）
        redisUtil.setRawString(ShopConstants.STOCK_KEY_PREFIX + product.getId(),
                String.valueOf(product.getStock()));
        product = getById(product.getId());
        ProductVo productVo = new ProductVo();
        BeanUtil.copyProperties(product, productVo);
        return productVo;
    }

    /**
     * 更新商品信息根据商品ID
     * @param req 更新商品请求
     * @return 更新后的商品VO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProductVo updateProduct(UpdateProductReq req) {
        Product product = new Product();
        BeanUtil.copyProperties(req, product);
        updateById(product);
        // 删除缓存（下次查询时自动重建）
        redisUtil.delete(ShopConstants.PRODUCT_CACHE_PREFIX + product.getId());
        // 如果修改了库存，同步更新 Redis 库存 key
        if (req.getStock() != null) {
            redisUtil.setRawString(ShopConstants.STOCK_KEY_PREFIX + product.getId(),
                    String.valueOf(req.getStock()));
        }
        ProductVo productVo = new ProductVo();
        BeanUtil.copyProperties(product, productVo);
        return productVo;
    }

    /**
     * 上架或下架商品根据商品ID（仅店铺或博主可以操作）
     * @param productId 商品ID
     * @param userId 用户ID/博主ID/店铺ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void upOrDownProduct(Long productId, Long userId) {
        Product product = getById(productId);
        if (Objects.isNull(product) || !Objects.equals(product.getShopId(), userId)) {
            throw new RuntimeException(ShopConstants.PRODUCT_NOT_EXIST_OR_NO_PERMISSION);
        }
        product.setStatus(Objects.equals(product.getStatus(), ShopConstants.PRODUCT_STATUS_ON_SHELF)
                ? ShopConstants.PRODUCT_STATUS_OFF_SHELF : ShopConstants.PRODUCT_STATUS_ON_SHELF);
        updateById(product);
        // 删除缓存（下次查询时自动重建）
        redisUtil.delete(ShopConstants.PRODUCT_CACHE_PREFIX + productId);
    }

    /**
     * 获取商品详情根据商品ID
     * @param productId 商品ID
     * @return 商品详情VO
     */
    @Override
    public ProductVo getProductDetail(Long productId) {
        String productCacheKey = ShopConstants.PRODUCT_CACHE_PREFIX + productId;

        // Cache-Aside：缓存命中直接返回，miss 则查库并自动回填
        Product product = redisUtil.getOrFetch(productCacheKey, Product.class,
                () -> {
                    Product p = getById(productId);
                    if (Objects.isNull(p)) {
                        throw new RuntimeException(ShopConstants.PRODUCT_NOT_EXIST_OR_NO_PERMISSION);
                    }
                    return p;
                }, ShopConstants.CACHE_EXPIRE_SECONDS, TimeUnit.SECONDS);

        // 热度 +1（无论是否命中缓存）
        redisUtil.incrementScore(ShopConstants.HOT_RANK_KEY, productId, 1);

        ProductVo productVo = new ProductVo();
        BeanUtil.copyProperties(product, productVo);
        return productVo;
    }

    /**
     * 获取店铺或博主的商品列表（可按店铺筛选）
     * @param shopId 店铺ID/博主ID
     * @param page 页码
     * @param size 每页数量
     * @return 商品列表VO
     */
    @Override
    public Page<Product> getProducts(Long shopId, int page, int size) {
        //根据店铺id查询商品列表，分页返回
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        if (shopId != null) {
            //根据店铺id查询商品列表
            wrapper.eq(Product::getShopId, shopId);
        }
        //根据创建时间降序排序
        wrapper.orderByDesc(Product::getCreateTime);
        //分页查询商品列表
        return page(new Page<>(page, size), wrapper);
    }

    /**
     * 获取热门商品列表
     * @param topN 热门商品数量限制
     * @return 热门商品列表VO
     */
    @Override
    public List<Product> getHotProducts(int topN) {
        Set<ZSetOperations.TypedTuple<Object>> hotSet = redisUtil
                .reverseRangeWithScores(ShopConstants.HOT_RANK_KEY, 0, topN - 1);
        if (hotSet == null || hotSet.isEmpty()) return Collections.emptyList();

        List<Long> productIds = hotSet.stream()
                .map(t -> Long.valueOf(Objects.requireNonNull(t.getValue()).toString()))
                .collect(Collectors.toList());
        return listByIds(productIds);
    }
}
