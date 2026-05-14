package com.smart.content.service;

import com.smart.content.domain.vo.TopicVO;
import com.smart.content.entity.TTopic;

import java.util.List;

/**
 * 话题服务接口
 */

public interface TopicService {

    /**
     * 创建话题
     * @param topicName 话题名
     * @return 话题实体
     */
    TTopic createTopic(String topicName);

    /**
     * 获取话题列表
     * @return 话题列表
     */
    List<TopicVO> getTopicList();

    /**
     * 删除话题
     * @param topicId 话题ID
     */
    void deleteTopic(Long topicId);
}
