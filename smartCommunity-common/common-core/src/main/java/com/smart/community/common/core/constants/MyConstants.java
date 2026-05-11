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


}
