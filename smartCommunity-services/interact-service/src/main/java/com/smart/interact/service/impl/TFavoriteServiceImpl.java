package com.smart.interact.service.impl;

import cn.hutool.core.util.StrUtil;

import com.component.redis.lock.RedisLockUtil;
import com.smart.community.common.core.constants.MyConstants;
import com.smart.community.common.core.constants.interact.InteractConstants;
import com.smart.community.common.core.constants.kafka.InteractKafkaConstants;
import com.smart.interact.domain.dto.FavoriteEventDTO;
import com.smart.interact.service.TFavoriteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class TFavoriteServiceImpl implements TFavoriteService {

    private final StringRedisTemplate redisTemplate;
    private final RedisLockUtil redisLockUtil;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * 收藏/取消收藏
     */
    public String toggleFavorite(Long userId, Long noteId) {
        String lockKey = InteractConstants.FAVORITE_LOCK_KEY + noteId + ":" + userId;
        String requestId = redisLockUtil.tryLock(lockKey, 5);
        if (StrUtil.isBlank(requestId)) {
            return InteractConstants.FAVORITE_OPERATION_FREQ_ERROR;
        }
        try {
            String setKey = InteractConstants.FAVORITE_USERS_KEY_PREFIX + noteId;
            String member = String.valueOf(userId);
            Boolean isMember = redisTemplate.opsForSet().isMember(setKey, member);
            boolean alreadyFavored = Boolean.TRUE.equals(isMember);

            int newStatus;
            if (alreadyFavored) {
                redisTemplate.opsForSet().remove(setKey, member);
                newStatus = InteractConstants.FAVORITE_STATUS_CANCEL;
            } else {
                redisTemplate.opsForSet().add(setKey, member);
                newStatus = InteractConstants.FAVORITE_STATUS_FAVORITE;
            }

            // 发送 Kafka 消息
            FavoriteEventDTO event = new FavoriteEventDTO(userId, noteId, newStatus, LocalDateTime.now());
            kafkaTemplate.send(InteractKafkaConstants.FAVORITE_EVENTS_TOPIC, String.valueOf(noteId), event);

            return alreadyFavored ? InteractConstants.FAVORITE_CANCEL_SUCCESS : InteractConstants.FAVORITE_SUCCESS;
        } finally {
            redisLockUtil.releaseLock(lockKey, requestId);
        }
    }
}