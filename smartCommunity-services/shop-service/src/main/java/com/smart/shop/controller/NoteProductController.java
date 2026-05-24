package com.smart.shop.controller;

import com.smart.community.common.core.constants.shop.ShopConstants;
import com.smart.community.common.core.result.Result;
import com.smart.shop.entity.Product;
import com.smart.shop.service.NoteProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shop")
@RequiredArgsConstructor
@Tag(name = "笔记商铺关联")
public class NoteProductController {

    private final NoteProductService noteProductService;


    @PostMapping("/addProductToNote")
    @Operation(summary = "笔记关联商品")
    public Result<String> addProductToNote(@RequestParam Long noteId, @RequestParam Long productId) {
        noteProductService.addProductToNote(noteId, productId);
        return Result.success(ShopConstants.NOTE_PRODUCT_ASSOC_SUCCESS);
    }

    @DeleteMapping("/removeProductFromNote")
    @Operation(summary = "笔记取消关联商品")
    public Result<String> removeProductFromNote(@RequestParam Long noteId, @RequestParam Long productId) {

        noteProductService.removeProductFromNote(noteId, productId);
        return Result.success(ShopConstants.NOTE_PRODUCT_ASSOC_REMOVE_SUCCESS);

    }

    @GetMapping("/getProductsByNote")
    @Operation(summary = "查询笔记关联的商品")
    public Result<List<Product>> getProductsByNote(@RequestParam Long noteId) {
        return Result.success(noteProductService.getProductsByNote(noteId));
    }
}
