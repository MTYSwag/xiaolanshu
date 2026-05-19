package com.smart.interact.service.impl;

import cn.hutool.core.util.StrUtil;
import com.component.redis.lock.RedisLockUtil;
import com.smart.community.common.core.constants.MyConstants;
import com.smart.community.common.core.constants.interact.InteractConstants;
import com.smart.community.common.core.constants.kafka.InteractKafkaConstants;
import com.smart.interact.domain.dto.LikeEventDTO;
import com.smart.interact.entity.TLike;
import com.smart.interact.mapper.TLikeMapper;
import com.smart.interact.service.TLikeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 点赞服务实现类
 *
 * 使用 Redis Set 维护点赞关系：
 *   Key:  note:like:users:{noteId}
 *   Value: Set&lt;userId&gt; -- 所有点赞用户的ID集合
 *   SCARD 天然即为点赞总数，无需单独维护计数
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TLikeServiceImpl extends ServiceImpl<TLikeMapper, TLike> implements TLikeService {

    private final StringRedisTemplate redisTemplate;
    private final RedisLockUtil redisLockUtil;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    //===============点赞/取消点赞（分布式锁 + Redis Set + Kafka）==============

    /**
     * 点赞/取消点赞
     * @param userId 用户ID
     * @param contentId 笔记ID
     * @return 操作结果消息
     */
    public String toggleLike(Long userId, Long contentId) {
        // 锁的key，粒度精确到"某用户对某笔记的操作"
        String lockKey = InteractConstants.LIKE_LOCK_KEY + contentId + ":" + userId;
        String requestId = redisLockUtil.tryLock(lockKey, 5);
        if (StrUtil.isBlank(requestId)) {
            return MyConstants.FREQUENT_ERROR_MSG;
        }
        try {
            // Redis Set: note:like:users:{noteId} -> Set<userId>
            String setKey = InteractConstants.LIKE_USERS_KEY_PREFIX + contentId;
            String member = String.valueOf(userId);

            // 判断当前是否已点赞：SISMEMBER O(1)
            Boolean isMember = redisTemplate.opsForSet().isMember(setKey, member);
            boolean alreadyLiked = Boolean.TRUE.equals(isMember);

            int newStatus;
            if (alreadyLiked) {
                // 取消点赞：从 Set 中移除
                redisTemplate.opsForSet().remove(setKey, member);
                newStatus = 0;
            } else {
                // 点赞：加入 Set（天然去重）
                redisTemplate.opsForSet().add(setKey, member);
                newStatus = 1;
            }

            // Kafka 异步入库
            LikeEventDTO event = new LikeEventDTO(userId, contentId, newStatus, LocalDateTime.now());
            kafkaTemplate.send(InteractKafkaConstants.LIKE_EVENTS_TOPIC, String.valueOf(contentId), event)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("Kafka 发送失败: topic={}, key={}", InteractKafkaConstants.LIKE_EVENTS_TOPIC, contentId, ex);
                        } else {
                            log.info("Kafka 发送成功: topic={}, partition={}, offset={}",
                                    result.getRecordMetadata().topic(),
                                    result.getRecordMetadata().partition(),
                                    result.getRecordMetadata().offset());
                        }
                    });

            return alreadyLiked ? MyConstants.LIKE_CANCEL_MSG : MyConstants.LIKE_SUCCESS_MSG;
        } finally {
            redisLockUtil.releaseLock(lockKey, requestId);
        }
    }

    /**
     * 查询某用户是否点赞了某笔记
     */
    public boolean hasLiked(Long userId, Long contentId) {
        String setKey = InteractConstants.LIKE_USERS_KEY_PREFIX + contentId;
        return Boolean.TRUE.equals(
                redisTemplate.opsForSet().isMember(setKey, String.valueOf(userId)));
    }

    /**
     * 获取笔记点赞总数
     */
    public Long getLikeCount(Long contentId) {
        String setKey = InteractConstants.LIKE_USERS_KEY_PREFIX + contentId;
        return redisTemplate.opsForSet().size(setKey);
    }
}