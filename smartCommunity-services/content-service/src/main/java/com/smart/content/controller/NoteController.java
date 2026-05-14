package com.smart.content.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smart.community.common.core.constants.note.NoteConstants;
import com.smart.community.common.core.result.Result;
import com.smart.content.domain.dto.DraftContentDTO;
import com.smart.content.domain.dto.PublishContentDTO;
import com.smart.content.domain.vo.NoteDetailVO;
import com.smart.content.domain.vo.NoteVO;
import com.smart.content.entity.TNoteDraft;
import com.smart.content.service.NoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/content")
@Tag(name = "笔记模块")
public class NoteController {

    @Autowired
    private NoteService noteService;

    @PostMapping("/publish")
    @Operation(summary = "发布笔记")
    public Result<Long> publish(@RequestBody PublishContentDTO form,
                                @RequestHeader("userId") Long userId) {
        if (StrUtil.isEmpty(form.getContent())) {
            return Result.fail(400, NoteConstants.NOTE_CONTENT_EMPTY);
        }
        Long noteId = noteService.publishNote(userId,form);
        return Result.success(noteId);
    }

    // ========== 草稿相关 ==========
    @PostMapping("/saveOrUpdateDraft")
    @Operation(summary = "新增或更新草稿")
    public Result<Long> saveOrUpdateDraft(@RequestHeader("userId") Long userId,
                                  @RequestBody DraftContentDTO form) {
        Long id = noteService.saveOrUpdateDraft(userId, form);
        return Result.success(id);
    }

    @GetMapping("/getDrafts")
    @Operation(summary = "获取草稿列表")
    public Result<List<TNoteDraft>> getDrafts(@RequestHeader("userId") Long userId) {
        return Result.success(noteService.getDrafts(userId));
    }

    @DeleteMapping("/deleteDraft/{draftId}")
    @Operation(summary = "删除草稿")
    public Result<String> deleteDraft(@RequestHeader("userId") Long userId,
                                      @RequestParam(value = "draftId")
                                      @Parameter(description = "草稿ID") Long draftId) {
        noteService.deleteDraft(draftId, userId);
        return Result.success("删除成功");
    }

    // ========== 笔记列表 ==========
    @GetMapping("/getMyNotes")
    @Operation(summary = "获取自己已发布的笔记（分页）")
    public Result<Page<NoteVO>> getMyNotes(@RequestHeader("userId") Long userId,
                                        @RequestParam(defaultValue = "1") int page,
                                        @RequestParam(defaultValue = "10") int size) {
        return Result.success(noteService.getMyNotes(userId, page, size));
    }

    @GetMapping("/getNotesByTopicId")
    @Operation(summary = "根据主题ID获取笔记列表（分页）")
    public Result<Page<NoteVO>> getNotesByTopicId(@RequestParam @Parameter(description = "主题ID") Long topicId,
                                             @RequestParam(defaultValue = "1") int page,
                                             @RequestParam(defaultValue = "10") int size) {
        return Result.success(noteService.getNotesByTopicId(topicId, page, size));
    }

    @GetMapping("/getNoteDetail")
    @Operation(summary = "获取笔记详情")
    public Result<NoteDetailVO> getNoteDetail(@RequestParam @Parameter(description = "笔记ID") Long noteId) {
        return Result.success(noteService.getNoteDetail(noteId));
    }

    @DeleteMapping("/deleteNote")
    @Operation(summary = "删除笔记")
    public Result<String> deleteNote(@RequestHeader("userId") Long userId,
                                     @RequestParam @Parameter(description = "笔记ID") Long noteId) {
        noteService.deleteNote(noteId, userId);
        return Result.success(NoteConstants.NOTE_DELETED);
    }

}