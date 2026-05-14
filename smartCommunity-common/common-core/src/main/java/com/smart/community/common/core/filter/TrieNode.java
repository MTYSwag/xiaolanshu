package com.smart.community.common.core.filter;

import java.util.HashMap;
import java.util.Map;

/**
 * 前缀树节点
 */
public class TrieNode {
    Map<Character, TrieNode> children = new HashMap<>();
    boolean isEnd = false; // 标记是否为敏感词结尾
}