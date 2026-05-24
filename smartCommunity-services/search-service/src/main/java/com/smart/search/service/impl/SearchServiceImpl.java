package com.smart.search.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Highlight;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.Suggestion;
import co.elastic.clients.elasticsearch.core.search.CompletionSuggestOption;
import com.smart.community.common.core.exception.BusinessException;
import com.smart.community.common.core.result.search.SearchResult;
import com.smart.search.document.NoteDocument;
import com.smart.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final ElasticsearchClient elasticsearchClient;

    @Override
    public SearchResult<NoteDocument> searchByKeyword(String keyword, int page, int size) {
        Query multiMatch = MultiMatchQuery.of(m -> m
                .query(keyword)
                .fields("title", "content"))._toQuery();

        Highlight highlight = Highlight.of(h -> h
                .fields("title", f -> f)
                .fields("content", f -> f));

        SearchRequest request = SearchRequest.of(s -> s
                .index("notes")
                .query(multiMatch)
                .highlight(highlight)
                .from((page - 1) * size)
                .size(size)
                .sort(sort -> sort.score(sc -> sc)));

        try {
            SearchResponse<NoteDocument> response = elasticsearchClient.search(request, NoteDocument.class);
            return buildResult(response);
        } catch (Exception e) {
            log.error("按关键词搜索失败", e);
            throw new BusinessException("按关键词搜索失败");
        }
    }

    @Override
    public SearchResult<NoteDocument> searchByTopic(String topic, int page, int size) {
        Query termQuery = TermQuery.of(t -> t.field("topics").value(topic))._toQuery();
        SearchRequest request = SearchRequest.of(s -> s
                .index("notes")
                .query(termQuery)
                .from((page - 1) * size)
                .size(size)
                .sort(sort -> sort.score(sc -> sc)));

        try {
            SearchResponse<NoteDocument> response = elasticsearchClient.search(request, NoteDocument.class);
            return buildResult(response);
        } catch (Exception e) {
            log.error("按话题搜索失败", e);
            throw new BusinessException("搜索话题搜索失败");
        }
    }

    @Override
    public SearchResult<NoteDocument> advancedSearchByKeyword(String keyword, int page, int size) {
        // 标题权重 2，内容权重 1
        Query titleMatch = MatchQuery.of(m -> m.field("title").query(keyword).boost(2F))._toQuery();
        Query contentMatch = MatchQuery.of(m -> m.field("content").query(keyword).boost(1F))._toQuery();

        SearchRequest request = SearchRequest.of(s -> s
                .index("notes")
                .query(q -> q.functionScore(fs -> fs
                        .query(BoolQuery.of(b -> b.should(titleMatch, contentMatch))._toQuery())
                        .functions(fn -> fn.fieldValueFactor(fvf -> fvf
                                .field("likeCount")
                                .factor(0.1)
                                .modifier(FieldValueFactorModifier.Log1p)))
                        .boostMode(FunctionBoostMode.Sum)
                        .scoreMode(FunctionScoreMode.Sum)))
                .highlight(Highlight.of(h -> h
                        .fields("title", f -> f)
                        .fields("content", f -> f)))
                .from((page - 1) * size)
                .size(size)
                .sort(sort -> sort.score(sc -> sc)));

        try {
            SearchResponse<NoteDocument> response = elasticsearchClient.search(request, NoteDocument.class);
            return buildResult(response);
        } catch (Exception e) {
            log.error("高级搜索失败", e);
            throw new BusinessException("搜索失败：" + e.getMessage());
        }
    }

    // ==================== 搜索建议（Completion Suggester）====================
    @Override
    public List<String> suggest(String prefix) {
        try {
            String escaped = prefix.replace("\\", "\\\\").replace("\"", "\\\"");
            String suggestJson = """
                    {"note-suggest":{"prefix":"%s","completion":{"field":"suggest","size":10}}}
                    """.formatted(escaped);

            // 单独构建 Suggester，再用 SearchRequest 引用
            co.elastic.clients.elasticsearch.core.search.Suggester suggester =
                    co.elastic.clients.elasticsearch.core.search.Suggester.of(s -> s
                            .withJson(new java.io.StringReader(suggestJson)));

            SearchRequest request = SearchRequest.of(s -> s
                    .index("notes")
                    .suggest(suggester));

            SearchResponse<Void> response = elasticsearchClient.search(request, Void.class);

            Map<String, List<Suggestion<Void>>> suggestMap = response.suggest();
            if (suggestMap == null) return List.of();

            List<Suggestion<Void>> suggestionList = suggestMap.get("note-suggest");
            if (suggestionList == null || suggestionList.isEmpty()) return List.of();

            List<String> result = new ArrayList<>();
            for (Suggestion<Void> suggestion : suggestionList) {
                if (suggestion.completion() != null) {
                    for (CompletionSuggestOption<Void> option : suggestion.completion().options()) {
                        result.add(option.text());
                    }
                }
            }
            return result;
        } catch (Exception e) {
            log.error("搜索建议查询失败", e);
            throw new BusinessException("搜索建议查询失败");
        }
    }

    // ==================== 结果构建（高亮 + search_after 游标）====================
    private SearchResult<NoteDocument> buildResult(SearchResponse<NoteDocument> response) {
        List<NoteDocument> docs = new ArrayList<>();
        List<Object> lastSort = null;

        List<Hit<NoteDocument>> hits = response.hits().hits();
        for (Hit<NoteDocument> hit : hits) {
            NoteDocument doc = hit.source();
            if (doc == null) continue;

            // 高亮字段覆盖原字段
            Map<String, List<String>> highlights = hit.highlight();
            if (highlights != null) {
                if (highlights.containsKey("title") && !highlights.get("title").isEmpty()) {
                    doc.setTitle(highlights.get("title").get(0));
                }
                if (highlights.containsKey("content") && !highlights.get("content").isEmpty()) {
                    doc.setContent(highlights.get("content").get(0));
                }
            }
            docs.add(doc);

            // 记录当前页最后一条的 sort 值 → 作为下一页的 search_after 游标
            if (hit.sort() != null && !hit.sort().isEmpty()) {
                lastSort = hit.sort().stream()
                        .map(fv -> {
                            if (fv.isDouble()) return fv.doubleValue();
                            if (fv.isLong()) return fv.longValue();
                            return fv.stringValue();
                        })
                        .collect(Collectors.toList());
            }
        }

        long total = response.hits().total() != null ? response.hits().total().value() : 0;
        SearchResult<NoteDocument> result = new SearchResult<>(total, docs);
        result.setSearchAfter(lastSort);
        return result;
    }
}