package com.smart.community.common.core.constants;

/**
 * 常量类
 */

public class MyConstants {

    /**
     * JWT 黑名单前缀,用于存储 Redis 中的 JWT 黑名单键，便于鉴权时快速判断 Token 是否在黑名单中
     */
    public static final String JWT_BLACKLIST = "jwt:blacklist:";

    /**
     * 未找到JWT令牌
     */
    public static final String JWT_NOT_FOUND = "JWT令牌未找到";

    /**
     * 400 错误码 , 表示客户端请求参数错误
     */
    public static final int ERROR_CODE_400 = 400;

    /**
     * 操作过于频繁，请稍后重试
     */
    public static final String FREQUENT_ERROR_MSG = "操作过于频繁，请稍后重试";

    /**
     * 取消点赞
     */
    public static final String LIKE_CANCEL_MSG = "取消点赞";

    /**
     * 点赞成功
     */
    public static final String LIKE_SUCCESS_MSG = "点赞成功";

}
