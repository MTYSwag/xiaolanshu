package com.smart.content.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smart.content.domain.dto.DraftContentDTO;
import com.smart.content.domain.dto.PublishContentDTO;
import com.smart.content.domain.vo.NoteDetailVO;
import com.smart.content.domain.vo.NoteVO;
import com.smart.content.entity.TNoteDraft;

import java.util.List;

public interface NoteService {

    /**
     * 发布笔记
     * @param userId  用户ID
     * @param form 笔记发布表单
     * @return 笔记ID
     */
    Long publishNote(Long userId, PublishContentDTO form);

    /**
     * 新增或更新草稿（如果已有草稿则更新）
     * @param userId 用户ID
     * @param form 草稿表单
     * @return 草稿ID
     */
    Long saveOrUpdateDraft(Long userId, DraftContentDTO form);

    /**
     * 获取用户草稿列表
     * @param userId 用户ID
     * @return 草稿列表
     */
    List<TNoteDraft> getDrafts(Long userId);

    /**
     * 删除草稿
     * @param draftId 草稿ID
     * @param userId 用户ID
     */
    void deleteDraft(Long draftId, Long userId);

    /**
     * 获取登录用户已发布的笔记列表
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页数量
     * @return 笔记列表页
     */
    Page<NoteVO> getMyNotes(Long userId, int page, int size);

    /**
     * 根据主题ID获取笔记列表（分页）
     * @param topicId 主题ID
     * @param page 页码
     * @param size 每页数量
     * @return 笔记列表页
     */
    Page<NoteVO> getNotesByTopicId(Long topicId, int page, int size);

    /**
     * 获取笔记详情
     * @param noteId 笔记ID
     * @return 笔记详情VO
     */
    NoteDetailVO getNoteDetail(Long noteId);

    /**
     * 删除笔记
     * @param noteId 笔记ID
     * @param userId 用户ID
     */
    void deleteNote(Long noteId, Long userId);
}
