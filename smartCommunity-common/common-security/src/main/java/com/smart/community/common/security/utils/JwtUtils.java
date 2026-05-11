package com.smart.community.common.security.utils;

import com.smart.community.common.core.constants.user.UserConstants;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtils {

    @Value("${jwt.secret:smartCommunity-secret-key-123456789-abcdefghijklmn}")
    private String secret;// 密钥

    @Value("${jwt.expiration:86400000}") // 默认24小时
    private long expiration;// 过期时间

    // 获取签名密钥
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成JWT token
     * @param claims 载荷信息（如userId）
     * @return token字符串
     */
    public String generateToken(Map<String, Object> claims) {
        return Jwts.builder()
                .claims(claims)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 生成带用户ID的token（简化版）
     */
    public String generateToken(String userId) {
        return generateToken(Map.of(UserConstants.USER_ID, userId));
    }

    /**
     * 解析token获取所有claims
     */
    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 从token中获取userId
     */
    public String getUserIdFromToken(String token) {
        return parseToken(token).get(UserConstants.USER_ID, String.class);
    }

    /**
     * 验证token是否有效
     */
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
