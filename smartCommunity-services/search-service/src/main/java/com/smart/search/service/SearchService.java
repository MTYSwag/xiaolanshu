package com.smart.search.service;

import com.smart.community.common.core.result.search.SearchResult;
import com.smart.search.document.NoteDocument;

import java.util.List;

public interface SearchService {

    /**
     * 按关键词搜索
     */
    SearchResult<NoteDocument> searchByKeyword(String keyword, int page, int size);

    /**
     * 按话题搜索
     */
    SearchResult<NoteDocument> searchByTopic(String topic, int page, int size);

    /**
     * 高级搜索（标题/内容加权 + 点赞数因子 + 高亮 + search_after 分页）
     */
    SearchResult<NoteDocument> advancedSearchByKeyword(String keyword, int page, int size);

    /**
     * 搜索建议（ES Completion Suggester 自动补全）
     * @param prefix 用户输入的前缀
     * @return 匹配的建议词列表
     */
    List<String> suggest(String prefix);
}