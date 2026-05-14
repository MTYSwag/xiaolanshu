package com.smart.community.common.core.filter;

import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;
import java.util.*;

/**
 * 敏感词过滤器
 */
@Component
public class SensitiveWordFilter {
    private final TrieNode root = new TrieNode();

    // 初始敏感词库（可从数据库或配置文件加载）
    private static final List<String> DEFAULT_WORDS = Arrays.asList(
            "敏感词1", "敏感词2", "广告", "违法", "赌博","色情","暴力","政治","约"
    );

    /**
     * 初始化敏感词过滤器
     */
    @PostConstruct
    public void init() {
        addWords(DEFAULT_WORDS);
    }

    /**
     * 批量添加敏感词
     * @param words 敏感词列表
     */
    public void addWords(List<String> words) {
        for (String word : words) {
            addWord(word);
        }
    }

    /**
     * 添加单个敏感词
     * @param word 敏感词
     */
    public void addWord(String word) {
        TrieNode node = root;
        for (char c : word.toCharArray()) {
            node.children.computeIfAbsent(c, k -> new TrieNode());
            node = node.children.get(c);
        }
        node.isEnd = true;
    }

    /**
     * 过滤文本，将敏感词替换为 **
     * @param text 原文本
     * @return 过滤后的文本
     */
    public String filter(String text) {
        if (text == null || text.isEmpty()) return text;
        StringBuilder result = new StringBuilder();
        int i = 0;
        while (i < text.length()) {
            TrieNode node = root;
            int j = i;
            int matchLen = -1;
            while (j < text.length() && node.children.containsKey(text.charAt(j))) {
                node = node.children.get(text.charAt(j));
                j++;
                if (node.isEnd) {
                    matchLen = j - i;
                }
            }
            if (matchLen > 0) {
                // 替换为 **
                for (int k = 0; k < matchLen; k++) {
                    result.append("*");
                }
                i += matchLen;
            } else {
                result.append(text.charAt(i));
                i++;
            }
        }
        return result.toString();
    }

    /**
     * 检测文本是否包含敏感词
     * @return true 包含
     */
    public boolean contains(String text) {
        if (text == null || text.isEmpty()) return false;
        for (int i = 0; i < text.length(); i++) {
            TrieNode node = root;
            for (int j = i; j < text.length(); j++) {
                char c = text.charAt(j);
                if (!node.children.containsKey(c)) break;
                node = node.children.get(c);
                if (node.isEnd) return true;
            }
        }
        return false;
    }
}