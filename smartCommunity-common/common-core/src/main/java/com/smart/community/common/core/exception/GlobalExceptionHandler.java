package com.smart.community.common.core.exception;

import com.smart.community.common.core.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * 全局异常处理类
 */

@Slf4j
@RestControllerAdvice //这个注解用于全局异常处理
//只有当应用是 Servlet 容器（MVC） 时才加载这个 Bean。Gateway 是 Reactive（WebFlux） 环境，因此不会被加载。
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass(DispatcherServlet.class)//进一步确保 DispatcherServlet 在类路径上。Gateway 中没有这个类，所以也不会加载。
public class GlobalExceptionHandler {

    // 处理业务异常
    @ExceptionHandler(BusinessException.class) //这个注解用于指定处理的异常类型
    public Result<?> handleBusinessException(BusinessException e) {
        log.warn("业务异常：{}", e.getMessage());
        return Result.fail(e.getCode(), e.getMessage());
    }

    // 处理其他未知异常
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        log.error("系统异常：", e);
        return Result.fail(500, "系统繁忙，请稍后再试");
    }

}
