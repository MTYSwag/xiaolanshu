package com.smart.interact.config;

import com.smart.community.common.core.constants.kafka.InteractKafkaConstants;
import com.smart.interact.domain.dto.FavoriteEventDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

/**
 * 收藏事件 Kafka 消费者独立配置
 */
@Slf4j
@Configuration
public class FavoriteKafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ConsumerFactory<String, FavoriteEventDTO> favoriteConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, InteractKafkaConstants.FAVORITE_GROUP_ID);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, true);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, FavoriteEventDTO.class.getName());
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, FavoriteEventDTO> favoriteKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, FavoriteEventDTO> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(favoriteConsumerFactory());
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(
                (record, exception) -> log.error("收藏事件消费失败（已重试3次）: topic={}, offset={}, key={}",
                        record.topic(), record.offset(), record.key(), exception),
                new FixedBackOff(1000L, 3L));
        errorHandler.addNotRetryableExceptions(org.apache.kafka.common.errors.SerializationException.class);
        factory.setCommonErrorHandler(errorHandler);
        return factory;
    }
}