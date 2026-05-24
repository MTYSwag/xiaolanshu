package com.smart.search.kafka.consumer;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.smart.search.document.NoteDocument;
import com.smart.search.domain.dto.NoteEventDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 笔记事件消费者 — 同步笔记到 ES
 * 使用 ElasticsearchClient 原生 API 索引文档，以便携带 completion suggest 字段
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NoteEventListener {

    private final ElasticsearchClient elasticsearchClient;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "note-events", groupId = "search-group")
    public void handleNoteEvent(String message) {
        try {
            NoteEventDTO event = objectMapper.readValue(message, NoteEventDTO.class);

            NoteDocument doc = new NoteDocument();
            doc.setId(event.getNoteId());
            doc.setUserId(event.getUserId());
            doc.setTitle(event.getTitle());
            doc.setContent(event.getContent());
            doc.setTopics(event.getTopics());
            doc.setLikeCount(0);
            doc.setCreateTime(event.getCreateTime());

            // 构建 suggest 字段的 input（标题 + 话题）
            List<String> inputs = new ArrayList<>();
            if (event.getTitle() != null) inputs.add(event.getTitle());
            if (event.getTopics() != null) inputs.addAll(event.getTopics());

            // 用 Jackson 构建包含 suggest 字段的完整文档 JSON
            ObjectNode docNode = objectMapper.valueToTree(doc);
            ObjectNode suggestNode = objectMapper.createObjectNode();
            ArrayNode inputArray = suggestNode.putArray("input");
            inputs.forEach(inputArray::add);
            suggestNode.put("weight", 1);
            docNode.set("suggest", suggestNode);

            // 用 ElasticsearchClient 原生 API 索引（绕过 Spring Data 反序列化问题）
            IndexRequest<ObjectNode> request = IndexRequest.of(i -> i
                    .index("notes")
                    .id(doc.getId().toString())
                    .document(docNode));

            elasticsearchClient.index(request);
            log.info("笔记已同步到 ES（含 suggest 字段）: noteId={}", event.getNoteId());
        } catch (Exception e) {
            log.error("同步笔记到 ES 失败", e);
        }
    }
}