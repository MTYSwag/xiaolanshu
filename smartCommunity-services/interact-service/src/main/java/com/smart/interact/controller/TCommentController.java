package com.smart.interact.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smart.community.common.core.result.Result;
import com.smart.interact.domain.req.CommentReq;
import com.smart.interact.domain.req.ReplyReq;
import com.smart.interact.entity.TComment;
import com.smart.interact.service.TCommentService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/interact")
@RequiredArgsConstructor
public class TCommentController {

    private final TCommentService tCommentService;

    // 添加一级评论
    @PostMapping("/addComment")
    @Schema(description = "添加一级评论")
    public Result<TComment> addComment(@RequestHeader("userId") Long userId,
                                      @RequestBody CommentReq req) {
        TComment comment = tCommentService.addComment(req, userId);
        return Result.success(comment);
    }

    // 回复评论
    @PostMapping("/replyComment")
    @Schema(description = "回复评论")
    public Result<TComment> replyComment(@RequestHeader("userId") Long userId,
                                 @RequestBody ReplyReq req) {
        TComment reply = tCommentService.replyComment(req, userId);
        return Result.success(reply);
    }

    // 一级评论列表
    @GetMapping("/getLevelOneComments/{noteId}")
    @Schema(description = "获取笔记下的一级评论列表")
    public Result<Page<TComment>> getLevelOneComments(@PathVariable @Parameter(description = "笔记ID") Long noteId,
                                              @RequestParam(defaultValue = "1") int page,
                                              @RequestParam(defaultValue = "10") int size) {
        return Result.success(tCommentService.getLevelOneComments(noteId, page, size));
    }

    // 回复列表
    @GetMapping("/getReplies/{commentId}")
    @Schema(description = "获取评论下的回复列表")
    public Result<Page<TComment>> getReplies(@PathVariable @Parameter(description = "要查询回复的评论ID") Long commentId,
                                             @RequestParam(defaultValue = "1") int page,
                                             @RequestParam(defaultValue = "10") int size) {
        return Result.success(tCommentService.getReplies(commentId, page, size));
    }
}