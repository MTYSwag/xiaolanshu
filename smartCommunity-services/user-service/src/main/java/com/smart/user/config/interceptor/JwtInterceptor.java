package com.smart.user.config.interceptor;

import com.smart.community.common.core.constants.MyConstants;
import com.smart.community.common.security.utils.JwtUtils;
import com.smart.user.config.context.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT拦截器
 * 用于验证JWT令牌并设置用户上下文
 * @author MTYSWAG
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtils jwtUtils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn(MyConstants.JWT_NOT_FOUND);
            response.setStatus(401);//设置响应状态码为401，表示未授权访问
            return false; //拦截请求
        }
        String token = authHeader.substring(7);
        try {
            String userId = jwtUtils.getUserIdFromToken(token);
            UserContext.setUserId(Long.valueOf(userId));
            return true;
        } catch (Exception e) {
            response.setStatus(401);
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.remove();
    }
}
