package com.smart.search.controller;

import com.smart.community.common.core.result.Result;
import com.smart.community.common.core.result.search.SearchResult;
import com.smart.search.document.NoteDocument;
import com.smart.search.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
@Tag(name = "搜索模块")
public class SearchController {

    private final SearchService searchService;


    @GetMapping("/searchByKeyword")
    @Operation(summary = "按关键词搜索")
    public Result<?> searchByKeyword(@RequestParam String keyword,
                            @RequestParam(defaultValue = "1") int page,
                            @RequestParam(defaultValue = "10") int size) {
        return Result.success(searchService.searchByKeyword(keyword, page, size));
    }


    @GetMapping("/searchByTopic")
    @Operation(summary = "按话题搜索")
    public Result<?> searchByTopic(@RequestParam String topic,
                                   @RequestParam(defaultValue = "1") int page,
                                   @RequestParam(defaultValue = "10") int size) {
        return Result.success(searchService.searchByTopic(topic, page, size));
    }

    @GetMapping("/advancedSearchByKeyword")
    @Operation(summary = "高级搜索（加权 + 高亮 + 分页）")
    public Result<SearchResult<NoteDocument>> advancedSearchByKeyword(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(searchService.advancedSearchByKeyword(keyword, page, size));
    }

    @GetMapping("/suggest")
    @Operation(summary = "搜索建议")
    public Result<List<String>> suggest(@RequestParam String prefix) {
        return Result.success(searchService.suggest(prefix));
    }
}