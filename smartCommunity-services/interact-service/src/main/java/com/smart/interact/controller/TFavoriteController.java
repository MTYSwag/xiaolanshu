package com.smart.interact.controller;

import com.smart.community.common.core.result.Result;
import com.smart.interact.service.TFavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/interact")
@RequiredArgsConstructor
public class TFavoriteController {

    private final TFavoriteService favoriteService;

    @Operation(summary = "收藏/取消收藏")
    @PostMapping("/toggleFavorite")
    public Result<String> toggleFavorite(@RequestHeader("userId") Long userId,
                                         @RequestParam Long noteId) {
        String msg = favoriteService.toggleFavorite(userId, noteId);
        if (msg.contains("频繁")) {
            return Result.fail(429, msg);
        }
        return Result.success(msg);
    }
}