package com.smart.shop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smart.community.common.core.constants.shop.ShopConstants;
import com.smart.shop.entity.NoteProduct;
import com.smart.shop.entity.Product;
import com.smart.shop.mapper.NoteProductMapper;
import com.smart.shop.mapper.ProductMapper;
import com.smart.shop.service.NoteProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoteProductServiceImpl implements NoteProductService {

    private final NoteProductMapper noteProductMapper;
    private final ProductMapper productMapper;

    /**
     * 为笔记添加商品链接
     * @param noteId 笔记id
     * @param productId 商品id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addProductToNote(Long noteId, Long productId) {
        NoteProduct exist = noteProductMapper.selectOne(new LambdaQueryWrapper<NoteProduct>()
                .eq(NoteProduct::getNoteId, noteId)
                .eq(NoteProduct::getProductId, productId));
        if (Objects.nonNull(exist)) {
            throw new RuntimeException(ShopConstants.NOTE_PRODUCT_ASSOC_EXIST);
        }
        NoteProduct np = new NoteProduct();
        np.setNoteId(noteId);
        np.setProductId(productId);
        noteProductMapper.insert(np);
    }

    /**
     * 从笔记中移除商品链接
     * @param noteId 笔记id
     * @param productId 商品id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeProductFromNote(Long noteId, Long productId) {
        NoteProduct np = noteProductMapper.selectOne(new LambdaQueryWrapper<NoteProduct>()
                .eq(NoteProduct::getNoteId, noteId)
                .eq(NoteProduct::getProductId, productId));
        if (Objects.isNull(np)) {
            throw new RuntimeException(ShopConstants.NOTE_PRODUCT_ASSOC_NOT_EXIST);
        }
        noteProductMapper.deleteById(np.getId());
    }

    @Override
    public List<Product> getProductsByNote(Long noteId) {
        List<NoteProduct> npList = noteProductMapper.selectList(
                new LambdaQueryWrapper<NoteProduct>().eq(NoteProduct::getNoteId, noteId));
        if (npList.isEmpty()) return Collections.emptyList();
        List<Long> productIds = npList.stream().map(NoteProduct::getProductId).collect(Collectors.toList());
        return productMapper.selectBatchIds(productIds);    }
}
