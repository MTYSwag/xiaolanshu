package com.smart.community.common.core.constants.interact;

/**
 * 互动模块常量类
 */
public class InteractConstants {

    /**
     * 点赞/取消点赞锁的key，粒度精确到"某用户对某笔记的操作"
     */
    public static final String LIKE_LOCK_KEY = "like:lock:";

    /**
     * 点赞用户集合 key 前缀 -- Redis Set，存 noteId 对应的所有点赞 userId
     */
    public static final String LIKE_USERS_KEY_PREFIX = "note:like:users:";

    /**
     *  收藏/取消收藏锁的key，粒度精确到"某用户对某笔记的操作"
     */
    public static final String FAVORITE_LOCK_KEY = "favorite:lock:";

    /**
     * 收藏用户集合 key 前缀 -- Redis Set，存 noteId 对应的所有收藏 userId
     */
    public static final String FAVORITE_USERS_KEY_PREFIX = "note:favorite:users:";

    /**
     * 收藏/取消收藏操作过于频繁错误提示
     */
    public static final String FAVORITE_OPERATION_FREQ_ERROR = "操作过于频繁，请稍后重试";

    /**
     * 收藏成功提示
     */
    public static final String FAVORITE_SUCCESS = "收藏成功";
    /**
     * 取消收藏成功提示
     */
    public static final String FAVORITE_CANCEL_SUCCESS = "取消收藏成功";

    /**
     * 取消收藏
     */
    public static final int FAVORITE_STATUS_CANCEL = 0;
    /**
     * 收藏成功
     */
    public static final int FAVORITE_STATUS_FAVORITE = 1;
}
