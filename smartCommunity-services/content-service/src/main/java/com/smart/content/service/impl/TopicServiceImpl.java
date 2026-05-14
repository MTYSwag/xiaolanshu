package com.smart.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.smart.community.common.core.constants.MyConstants;
import com.smart.community.common.core.constants.note.TopicConstants;
import com.smart.community.common.core.exception.BusinessException;
import com.smart.content.domain.vo.TopicVO;
import com.smart.content.entity.TNoteTopic;
import com.smart.content.entity.TTopic;
import com.smart.content.mapper.TNoteTopicMapper;
import com.smart.content.mapper.TTopicMapper;
import com.smart.content.service.TopicService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 话题服务实现类
 */

@Service
@RequiredArgsConstructor
public class TopicServiceImpl extends ServiceImpl<TTopicMapper, TTopic> implements TopicService {


    private final TNoteTopicMapper noteTopicMapper;
    /**
     * 创建话题
     * @param topicName 话题名
     * @return 话题实体
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public TTopic createTopic(String topicName) {
        TTopic isExistTopic = this.getOne(new LambdaQueryWrapper<TTopic>().eq(TTopic::getName, topicName));
        if (Objects.nonNull(isExistTopic)) {
            throw new BusinessException(MyConstants.ERROR_CODE_400, TopicConstants.TOPIC_NAME_EXIST);
        }
        TTopic topic = new TTopic();
        topic.setName(topicName);
        this.save(topic);
        return topic;
    }

    /**
     * 获取话题列表（含笔记计数）
     * @return 话题列表
     */
    @Override
    public List<TopicVO> getTopicList() {
        // 1. 查询所有话题
        List<TTopic> topics = this.list();
        // 2. 查询每个话题关联的笔记数
        return topics.stream().map(topic -> {
            TopicVO topicVO = new TopicVO();
            topicVO.setId(topic.getId());
            topicVO.setName(topic.getName());
            Long noteCount = noteTopicMapper.selectCount(
                    new LambdaQueryWrapper<TNoteTopic>().eq(TNoteTopic::getTopicId, topic.getId()));
            topicVO.setNoteCount(noteCount);
            return topicVO;
        }).collect(Collectors.toList());
    }

    /**
     * 删除话题（同时删除关联记录）
     * @param topicId 话题ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTopic(Long topicId) {
        this.removeById(topicId);
        noteTopicMapper.delete(new LambdaQueryWrapper<TNoteTopic>().eq(TNoteTopic::getTopicId, topicId));
    }


}
