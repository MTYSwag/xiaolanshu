package com.smart.user.config.util;

import cn.hutool.crypto.SecureUtil;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 密码加密工具类
 */

public class PasswordUtil {
    /**
     * 生成随机盐值
     * @return 随机盐值
     */
    public static String generateSalt() {
        byte[] bytes = new byte[16];
        new SecureRandom().nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * 对密码进行加盐哈希加密
     * @param password 明文密码
     * @param salt 盐值
     * @return 加密后的密码字符串
     */
    public static String encrypt(String password, String salt) {
        return SecureUtil.md5(password + salt);
    }
}