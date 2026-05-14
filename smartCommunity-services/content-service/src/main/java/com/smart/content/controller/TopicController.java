package com.smart.content.controller;

import cn.hutool.core.util.StrUtil;
import com.smart.community.common.core.constants.MyConstants;
import com.smart.community.common.core.constants.note.TopicConstants;
import com.smart.community.common.core.result.Result;
import com.smart.content.domain.dto.CreateTopicDTO;
import com.smart.content.domain.vo.TopicVO;
import com.smart.content.entity.TTopic;
import com.smart.content.service.TopicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/topic")
@Tag(name = "话题模块")
public class TopicController {

    private final TopicService topicService;

    @PostMapping("/createTopic")
    @Operation(summary = "创建话题")
    public Result<TTopic> createTopic(@RequestParam @Parameter(description = "话题名") String topicName) {
        if (StrUtil.isEmpty(topicName)) {
            return Result.fail(MyConstants.ERROR_CODE_400, TopicConstants.TOPIC_NAME_EMPTY);
        }
        return Result.success(topicService.createTopic(topicName));
    }

    @GetMapping("/getTopicList")
    @Operation(summary = "获取话题列表")
    public Result<List<TopicVO>> getTopicList() {
        return Result.success(topicService.getTopicList());
    }

    @DeleteMapping("/deleteTopic")
    @Operation(summary = "删除话题")
    public Result<String> deleteTopic(@RequestParam @Parameter(description = "话题ID") Long topicId) {
        topicService.deleteTopic(topicId);
        return Result.success(TopicConstants.TOPIC_NAME_DELETED);
    }
}
