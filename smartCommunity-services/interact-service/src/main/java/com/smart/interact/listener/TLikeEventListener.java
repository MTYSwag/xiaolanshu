package com.smart.interact.listener;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smart.community.common.core.constants.kafka.InteractKafkaConstants;
import com.smart.interact.domain.dto.LikeEventDTO;
import com.smart.interact.entity.TLike;
import com.smart.interact.mapper.TLikeMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * kafka 点赞事件监听器（异步入库）
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class TLikeEventListener {

    private final TLikeMapper likeMapper;

    @PostConstruct
    public void init() {
        log.info("TLikeEventListener 已注册，监听 topic={}, groupId={}",
                InteractKafkaConstants.LIKE_EVENTS_TOPIC, InteractKafkaConstants.LIKE_GROUP_ID);
    }

    @KafkaListener(topics = InteractKafkaConstants.LIKE_EVENTS_TOPIC,
            groupId = InteractKafkaConstants.LIKE_GROUP_ID,
            containerFactory = "likeKafkaListenerContainerFactory")
    public void handleLikeEvent(LikeEventDTO event) {
        log.info("收到点赞事件: {}", event);
        // 幂等处理：先查后插/更新
        TLike existLikeEvent = likeMapper.selectOne(new LambdaQueryWrapper<TLike>()
                .eq(TLike::getUserId, event.getUserId())
                .eq(TLike::getContentId, event.getContentId()));
        if (!Objects.isNull(existLikeEvent)) {
            log.info("点赞事件已存在！");
            existLikeEvent.setStatus(event.getStatus());
            likeMapper.updateById(existLikeEvent);
        } else {
            TLike like = new TLike();
            like.setUserId(event.getUserId());
            like.setContentId(event.getContentId());
            like.setStatus(event.getStatus());
            likeMapper.insert(like);
        }
    }
}