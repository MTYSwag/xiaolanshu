package com.smart.shop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smart.community.common.core.constants.shop.ShopConstants;
import com.smart.community.common.core.exception.BusinessException;
import com.smart.shop.entity.Product;
import com.smart.shop.entity.Showcase;
import com.smart.shop.mapper.ProductMapper;
import com.smart.shop.mapper.ShowcaseMapper;
import com.smart.shop.service.ShowcaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShowcaseServiceImpl implements ShowcaseService {


    private final ProductMapper productMapper;
    private final ShowcaseMapper showcaseMapper;
    /**
     * 加入橱窗
     * @param userId 用户id
     * @param productId 商品id
     * @param sortOrder 排序
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addToShowcase(Long userId, Long productId, Integer sortOrder) {
        // 加入橱窗
        Product product = productMapper.selectById(productId);
        if (Objects.isNull(product) || !Objects.equals(product.getShopId(), userId)) {
            throw new BusinessException(ShopConstants.NO_PERMISSION_TO_OPERATE);
        }
        Showcase exist = showcaseMapper.selectOne(new LambdaQueryWrapper<Showcase>()
                .eq(Showcase::getShopId, userId)
                .eq(Showcase::getProductId, productId));
        if (exist != null) {
            throw new BusinessException(ShopConstants.PRODUCT_EXIST_IN_SHOWCASE);
        }
        Showcase sc = new Showcase();
        sc.setShopId(userId);
        sc.setProductId(productId);
        sc.setSortOrder(sortOrder != null ? sortOrder : 0);
        sc.setStatus(1);
        showcaseMapper.insert(sc);
    }

    /**
     * 移除橱窗商品
     * @param userId 用户id
     * @param productId 商品id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeShowcase(Long userId, Long productId) {
        Showcase sc = showcaseMapper.selectOne(new LambdaQueryWrapper<Showcase>()
                .eq(Showcase::getShopId, userId)
                .eq(Showcase::getProductId, productId));
        if (Objects.isNull(sc)) {
            throw new BusinessException(ShopConstants.PRODUCT_NOT_EXIST_OR_NO_PERMISSION);
        }
        showcaseMapper.deleteById(sc.getId());
    }

    /**
     * 查看博主橱窗
     * @param shopId 店铺id
     * @return 商品列表
     */
    @Override
    public List<Product> getShowcaseProducts(Long shopId) {
        List<Showcase> scList = showcaseMapper.selectList(
                new LambdaQueryWrapper<Showcase>()
                        .eq(Showcase::getShopId, shopId)
                        .eq(Showcase::getStatus, 1)
                        .orderByAsc(Showcase::getSortOrder));
        if (scList.isEmpty()) return Collections.emptyList();
        List<Long> productIds = scList.stream().map(Showcase::getProductId).collect(Collectors.toList());
        return productMapper.selectBatchIds(productIds);
    }
}
