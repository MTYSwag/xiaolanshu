package com.smart.interact.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smart.interact.domain.req.CommentReq;
import com.smart.interact.domain.req.ReplyReq;
import com.smart.interact.entity.TComment;

/**
 * 评论服务
 */
public interface TCommentService {

    /**
     * 添加评论
     * @param req 评论请求参数
     * @param userId 用户ID
     * @return 评论实体
     */
    TComment addComment(CommentReq req, Long userId);

    /**
     * 回复评论
     * @param req 回复评论请求参数
     * @param userId 用户ID
     * @return 评论实体
     */
    TComment replyComment(ReplyReq req, Long userId);

    /**
     * 获取一级评论
     * @param noteId 笔记ID
     * @param page 页码
     * @param size 每页数量
     * @return 一级评论分页结果
     */
    Page<TComment> getLevelOneComments(Long noteId, int page, int size);

    /**
     * 获取回复评论
     * @param commentId 评论ID
     * @param page 页码
     * @param size 每页数量
     * @return 回复评论分页结果
     */
    Page<TComment> getReplies(Long commentId, int page, int size);
}
