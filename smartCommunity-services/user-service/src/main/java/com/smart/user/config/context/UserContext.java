package com.smart.user.config.context;

/**
 * 用户上下文类
 * 用于存储和获取当前线程的用户ID(线程级别)
 */

public class UserContext {
    /**
     * 用户ID线程本地存储
     * 用于存储当前线程的用户ID(线程级别)
     */
    private static final ThreadLocal<Long> USER_ID_HOLDER = new ThreadLocal<>();

    public static void setUserId(Long userId) {
        USER_ID_HOLDER.set(userId);
    }

    public static Long getUserId() {
        return USER_ID_HOLDER.get(); // 可能为 null
    }

    public static void remove() {
        USER_ID_HOLDER.remove();
    }
}
