package com.smart.content.service.impl;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smart.community.common.core.constants.MyConstants;
import com.smart.community.common.core.constants.note.NoteConstants;
import com.smart.community.common.core.exception.BusinessException;
import com.smart.community.common.core.filter.SensitiveWordFilter;
import com.smart.content.domain.dto.DraftContentDTO;
import com.smart.content.domain.dto.NoteEventDTO;
import com.smart.content.domain.dto.PublishContentDTO;
import com.smart.content.domain.vo.NoteDetailVO;
import com.smart.content.domain.vo.NoteVO;
import com.smart.content.entity.*;
import com.smart.content.mapper.*;
import com.smart.content.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 笔记服务实现类
 */
@Service
@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService {

    private final TNoteMapper noteMapper;
    private final TNoteImageMapper imageMapper;
    private final TNoteTopicMapper noteTopicMapper;
    private final TTopicMapper topicMapper;
    private final KafkaTemplate<String, NoteEventDTO> kafkaTemplate;
    private final TNoteDraftMapper noteDraftMapper;
    /**
     * 笔记事件主题
     */
    private static final String NOTE_EVENT_TOPIC = NoteConstants.NOTE_EVENT_TOPIC;

    /**
     * 敏感词过滤器
     */
    @Autowired
    private SensitiveWordFilter sensitiveWordFilter;



    /**
     * 发布笔记
     *
     * @param userId 用户ID
     * @param form   笔记发布表单
     * @return 笔记ID
     */
    @Transactional(rollbackFor = Exception.class)
    public Long publishNote(Long userId, PublishContentDTO form) {

        String title = form.getTitle(); // 笔记标题
        String content = form.getContent(); // 笔记内容
        List<String> imageUrls = form.getImageUrls();
        List<String> topicNames = form.getTopics();


        // 过滤敏感词 planA: 直接过滤标题和内容
        title = sensitiveWordFilter.filter(title);
        content = sensitiveWordFilter.filter(content);
        //planB: 检测标题和内容是否包含敏感词,如果包含则抛出异常,否则继续执行
        if (sensitiveWordFilter.contains(title) || sensitiveWordFilter.contains(content)) {
            throw new BusinessException("标题或内容包含敏感词");
        }


        // 1. 保存笔记主体
        TNote note = new TNote();
        note.setUserId(userId);
        note.setTitle(title);
        note.setContent(content);
        noteMapper.insert(note);

        // 2. 保存图片（如果有）
        if (imageUrls != null) {
            for (int i = 0; i < imageUrls.size(); i++) {
                TNoteImage img = new TNoteImage();
                img.setNoteId(note.getId());
                img.setUrl(imageUrls.get(i));
                img.setSortOrder(i);
                imageMapper.insert(img);
            }
        }

        // 3. 处理话题（如果话题不存在则创建）
        List<String> finalTopics = new ArrayList<>();
        if (!Objects.isNull(topicNames)) {
            for (String name : topicNames) {
                TTopic topic = topicMapper.selectOne(
                        new LambdaQueryWrapper<TTopic>().eq(TTopic::getName, name));
                if (Objects.isNull(topic)) {
                    topic = new TTopic();
                    topic.setName(name);
                    topicMapper.insert(topic);
                }
                TNoteTopic nt = new TNoteTopic();
                nt.setNoteId(note.getId());
                nt.setTopicId(topic.getId());
                noteTopicMapper.insert(nt);
                finalTopics.add(name);
            }
        }

        // 4. 发送 Kafka 消息（异步解耦）
        NoteEventDTO event = new NoteEventDTO();
        event.setNoteId(note.getId());
        event.setUserId(userId);
        event.setTitle(title);
        event.setContent(content);
        event.setImageUrls(imageUrls);
        event.setTopics(finalTopics);
        event.setCreateTime(LocalDateTime.now());
        // 消息：key=笔记ID, value=笔记事件体
        String key = String.valueOf(note.getId());
        NoteEventDTO value = event;
        kafkaTemplate.send(NOTE_EVENT_TOPIC, key, value);

        return note.getId();
    }

    /**
     * 新增或更新草稿（如果已有草稿则更新）
     *
     * @param userId 用户ID
     * @param form   草稿表单
     * @return 草稿ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveOrUpdateDraft(Long userId, DraftContentDTO form) {
        Long draftId = form.getDraftId();

        TNoteDraft draft = (draftId != null) ? noteDraftMapper.selectById(draftId) : null;

        if (Objects.isNull(draft)) {
            draft = new TNoteDraft();
            draft.setUserId(userId);
            draft.setTitle(form.getTitle());
            draft.setContent(form.getContent());
            draft.setIsDeleted(NoteConstants.NORMAL_STATUS);
            noteDraftMapper.insert(draft);
        } else {
            draft.setTitle(form.getTitle());
            draft.setContent(form.getContent());
            noteDraftMapper.updateById(draft);
        }
        return draft.getId();
    }

    /**
     * 获取用户草稿列表
     *
     * @param userId 用户ID
     * @return 草稿列表
     */
    @Override
    public List<TNoteDraft> getDrafts(Long userId) {
        return noteDraftMapper.selectList(
                new LambdaQueryWrapper<TNoteDraft>()
                        .eq(TNoteDraft::getUserId, userId)
                        .eq(TNoteDraft::getIsDeleted, NoteConstants.NORMAL_STATUS)
                        .orderByDesc(TNoteDraft::getUpdateTime));
    }

    /**
     * 删除草稿（软删除）
     *
     * @param draftId 草稿ID
     * @param userId  用户ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDraft(Long draftId, Long userId) {
        TNoteDraft draft = noteDraftMapper.selectById(draftId);
        if (Objects.isNull(draft) || !draft.getUserId().equals(userId)) {
            throw new BusinessException(403, NoteConstants.NO_PERMISSION_TO_DELETE_DRAFT);
        }
        draft.setIsDeleted(NoteConstants.DELETED_STATUS);
        noteDraftMapper.updateById(draft);
    }


    /**
     * 查询用户已发布的笔记（分页）
     *
     * @param userId 用户ID
     * @param page   页码
     * @param size   每页数量
     * @return 笔记列表VO
     */
    @Override
    public Page<NoteVO> getMyNotes(Long userId, int page, int size) {
        Page<TNote> notePage = noteMapper.selectPage(
                new Page<>(page, size),
                new LambdaQueryWrapper<TNote>()
                        .eq(TNote::getUserId, userId)
                        .eq(TNote::getIsDeleted, 0)
                        .orderByDesc(TNote::getCreateTime));
        return convertToVO(notePage);
    }

    // 转换为VO(将TNote转换为NoteVO，避免直接返回实体类)
    private Page<NoteVO> convertToVO(Page<TNote> notePage) {
        Page<NoteVO> voPage = new Page<>(notePage.getCurrent(), notePage.getSize(), notePage.getTotal());
        voPage.setRecords(notePage.getRecords().stream().map(note -> {
            NoteVO vo = new NoteVO();
            vo.setId(note.getId());
            vo.setTitle(note.getTitle());
            vo.setContent(note.getContent());
            vo.setCreateTime(note.getCreateTime());
            return vo;
        }).collect(Collectors.toList()));
        return voPage;
    }

    /**
     * 根据主题ID查询笔记（分页）
     *
     * @param topicId 主题ID
     * @param page    页码
     * @param size    每页数量
     * @return 笔记列表VO
     */
    @Override
    public Page<NoteVO> getNotesByTopicId(Long topicId, int page, int size) {
        // 先查关联表 note_topic 获取笔记 ID 列表
        Page<TNoteTopic> noteTopicPage = noteTopicMapper.selectPage(
                new Page<>(page, size),
                new LambdaQueryWrapper<TNoteTopic>()
                        .eq(TNoteTopic::getTopicId, topicId));
        List<Long> noteIds = noteTopicPage.getRecords().stream()
                .map(TNoteTopic::getNoteId).collect(Collectors.toList());
        if (noteIds.isEmpty()) {
            return new Page<>(page, size, noteTopicPage.getTotal());
        }
        // 再查笔记
        List<TNote> notes = noteMapper.selectBatchIds(noteIds);
        // 填充用户昵称（需要 Feign 调用 user 服务，这里暂时用硬编码或后续再补）
        Page<NoteVO> voPage = new Page<>(page, size, noteTopicPage.getTotal());
        voPage.setRecords(notes.stream().map(note -> {
            NoteVO vo = new NoteVO();
            vo.setId(note.getId());
            vo.setTitle(note.getTitle());
            vo.setContent(note.getContent());
            vo.setCreateTime(note.getCreateTime());
            return vo;
        }).collect(Collectors.toList()));
        return voPage;
    }

    /**
     * 获取笔记详情
     *
     * @param noteId 笔记ID
     * @return 笔记详情VO
     */
    @Override
    public NoteDetailVO getNoteDetail(Long noteId) {

        TNote note = noteMapper.selectById(noteId);
        if (Objects.isNull(note) || Objects.equals(note.getIsDeleted(), NoteConstants.NORMAL_STATUS)) {
            throw new BusinessException(404, NoteConstants.NOTE_NOT_FOUND);
        }
        NoteDetailVO noteDetailVO = new NoteDetailVO();
        BeanUtil.copyProperties(note, noteDetailVO);

        // 图片列表
        List<TNoteImage> images = imageMapper.selectList(
                new LambdaQueryWrapper<TNoteImage>()
                        .eq(TNoteImage::getNoteId, noteId)
                        .eq(TNoteImage::getIsDeleted, NoteConstants.NORMAL_STATUS)
                        .orderByAsc(TNoteImage::getSortOrder));
        noteDetailVO.setImageUrls(images.stream().map(TNoteImage::getUrl).collect(Collectors.toList()));

        // 话题列表
        List<TNoteTopic> noteTopics = noteTopicMapper.selectList(
                new LambdaQueryWrapper<TNoteTopic>().eq(TNoteTopic::getNoteId, noteId));
        if (!noteTopics.isEmpty()) {
            List<Long> topicIds = noteTopics.stream().map(TNoteTopic::getTopicId).collect(Collectors.toList());
            List<TTopic> topics = topicMapper.selectBatchIds(topicIds);
            noteDetailVO.setTopics(topics.stream().map(TTopic::getName).collect(Collectors.toList()));
        } else {
            noteDetailVO.setTopics(Collections.emptyList());
        }

        // 作者昵称暂时用硬编码，后续用 Feign 调用 user 服务
        noteDetailVO.setUserName("用户" + note.getUserId());

        return noteDetailVO;
    }

    /**
     * 删除笔记(逻辑删除/软删除)
     * @param noteId 笔记ID
     * @param userId 用户ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteNote(Long noteId, Long userId) {
        TNote note = noteMapper.selectById(noteId);
        if (Objects.isNull(note)) {
            throw new BusinessException(404, NoteConstants.NOTE_NOT_FOUND);
        }
        if (!note.getUserId().equals(userId)) {
            throw new BusinessException(403, NoteConstants.NO_PERMISSION_TO_DELETE_DRAFT);
        }
        note.setIsDeleted(NoteConstants.DELETED_STATUS);
        noteMapper.updateById(note);

        // 软删除关联图片
        List<TNoteImage> images = imageMapper.selectList(
                new LambdaQueryWrapper<TNoteImage>().eq(TNoteImage::getNoteId, noteId));
        if (images != null) {
            for (TNoteImage img : images) {
                img.setIsDeleted(NoteConstants.DELETED_STATUS);
                imageMapper.updateById(img);
            }
        }
    }
}


