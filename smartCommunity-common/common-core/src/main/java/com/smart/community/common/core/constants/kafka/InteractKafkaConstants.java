package com.smart.community.common.core.constants.kafka;

/**
 * kafka-interact模块常量类
 */
public class InteractKafkaConstants {

    /**
     * 点赞事件 Kafka 主题
     */
    public static final String LIKE_EVENTS_TOPIC = "like-events";

    /**
     * 收藏事件 Kafka 主题
     */
    public static final String FAVORITE_EVENTS_TOPIC = "favorite-events";

    /**
     * 点赞事件消费组
     */
    public static final String LIKE_GROUP_ID = "interact-like-group";

    /**
     * 收藏事件消费组
     */
    public static final String FAVORITE_GROUP_ID = "interact-favorite-group";
}
