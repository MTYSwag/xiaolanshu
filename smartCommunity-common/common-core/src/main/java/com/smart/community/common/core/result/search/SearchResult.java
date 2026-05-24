package com.smart.community.common.core.result.search;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * 搜索结果包装
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchResult<T> {
    private long total;
    private List<T> data;
    /**
     * search_after 分页游标：当前页最后一条记录的排序值
     * 客户端请求下一页时传回此值，即可实现深度分页
     * null 表示已是最后一页
     */
    private List<Object> searchAfter;

    public SearchResult(long total, List<T> data) {
        this.total = total;
        this.data = data;
    }
}