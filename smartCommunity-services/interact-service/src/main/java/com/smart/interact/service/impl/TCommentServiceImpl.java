package com.smart.interact.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smart.interact.domain.req.CommentReq;
import com.smart.interact.domain.req.ReplyReq;
import com.smart.interact.entity.TComment;
import com.smart.interact.mapper.TCommentMapper;
import com.smart.interact.service.TCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TCommentServiceImpl implements TCommentService {

    private final TCommentMapper commentMapper;

    /**
     * 添加一级评论
     * @param req 评论请求参数
     * @param userId 用户ID
     * @return 评论实体
     */
    @Override
    public TComment addComment(CommentReq req, Long userId) {
        TComment comment = new TComment();
        comment.setNoteId(req.getNoteId());
        comment.setUserId(userId);
        comment.setContent(req.getContent());
        comment.setParentId(0L);
        commentMapper.insert(comment);
        return comment;
    }

    /**
     * 回复评论
     * @param req 回复评论请求参数
     * @param userId 用户ID
     * @return 回复评论实体
     */
    @Override
    public TComment replyComment(ReplyReq req, Long userId) {
        TComment reply = new TComment();
        reply.setNoteId(req.getNoteId());
        reply.setUserId(userId);
        reply.setContent(req.getContent());
        reply.setParentId(req.getParentId());
        reply.setReplyToUserId(req.getReplyToUserId());
        commentMapper.insert(reply);
        return reply;
    }


    /**
     * 分页查询一级评论（按时间倒序）
     * @param noteId 笔记ID
     * @param page 页码
     * @param size 每页数量
     * @return 一级评论分页结果
     */
    @Override
    public Page<TComment> getLevelOneComments(Long noteId, int page, int size) {
        return commentMapper.selectPage(
                new Page<>(page, size),
                new LambdaQueryWrapper<TComment>()
                        .eq(TComment::getNoteId, noteId)
                        .eq(TComment::getParentId, 0L)
                        .eq(TComment::getIsDeleted, 0)
                        .orderByDesc(TComment::getCreateTime)
        );
    }

    /**
     * 分页查询某条评论的回复（按时间正序）
     * @param commentId 评论ID
     * @param page 页码
     * @param size 每页数量
     * @return 回复分页结果
     */
    @Override
    public Page<TComment> getReplies(Long commentId, int page, int size) {
        return commentMapper.selectPage(
                new Page<>(page, size),
                new LambdaQueryWrapper<TComment>()
                        .eq(TComment::getParentId, commentId)
                        .eq(TComment::getIsDeleted, 0)
                        .orderByAsc(TComment::getCreateTime)
        );
    }
}