package com.smart.interact.service;

/**
 * 点赞服务接口
 */
public interface TLikeService {


    String toggleLike(Long userId, Long contentId);
}
