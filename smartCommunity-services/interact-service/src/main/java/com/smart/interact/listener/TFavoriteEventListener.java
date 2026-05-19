package com.smart.interact.listener;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smart.community.common.core.constants.kafka.InteractKafkaConstants;
import com.smart.interact.domain.dto.FavoriteEventDTO;
import com.smart.interact.entity.TFavorite;
import com.smart.interact.mapper.TFavoriteMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TFavoriteEventListener {

    private final TFavoriteMapper favoriteMapper;

    @KafkaListener(topics = InteractKafkaConstants.FAVORITE_EVENTS_TOPIC,
            groupId = InteractKafkaConstants.FAVORITE_GROUP_ID,
            containerFactory = "favoriteKafkaListenerContainerFactory")
    public void handleFavoriteEvent(FavoriteEventDTO event) {
        log.info("收到收藏事件：{}", event);
        TFavorite exist = favoriteMapper.selectOne(
                new LambdaQueryWrapper<TFavorite>()
                        .eq(TFavorite::getUserId, event.getUserId())
                        .eq(TFavorite::getNoteId, event.getNoteId()));
        if (exist != null) {
            exist.setStatus(event.getStatus());
            favoriteMapper.updateById(exist);
        } else {
            TFavorite fav = new TFavorite();
            fav.setUserId(event.getUserId());
            fav.setNoteId(event.getNoteId());
            fav.setStatus(event.getStatus());
            favoriteMapper.insert(fav);
        }
    }
}