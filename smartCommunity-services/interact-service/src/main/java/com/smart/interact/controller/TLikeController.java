package com.smart.interact.controller;


import com.smart.community.common.core.result.Result;
import com.smart.interact.service.TLikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/interact/")
@Tag(name = "互动模块")
public class TLikeController {

    @Autowired
    private TLikeService tLikeService;

    @PostMapping("/toggleLike")
    @Operation(summary = "点赞/取消点赞")
    public Result<String> toggleLike(@RequestHeader("userId") Long userId,
                                     @RequestParam Long contentId) {
        String msg = tLikeService.toggleLike(userId, contentId);
        if (msg.contains("频繁")) {
            return Result.fail(429, msg);
        }
        return Result.success(msg);
    }


}
