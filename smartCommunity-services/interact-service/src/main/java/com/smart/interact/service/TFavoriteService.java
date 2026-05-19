package com.smart.interact.service;

/**
 * 收藏服务
 */
public interface TFavoriteService {

    /**
     * 收藏/取消收藏
     * @param userId 用户ID
     * @param noteId 笔记ID
     * @return 提示信息
     */
    String toggleFavorite(Long userId, Long noteId);
}
