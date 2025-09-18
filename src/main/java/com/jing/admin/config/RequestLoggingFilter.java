package com.jing.admin.config;

import com.jing.admin.core.ThreadMdcUtils;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.UUID;

/**
 * @author lxh
 * @date 2025/9/18
 **/
@Component
@Order(Ordered.HIGHEST_PRECEDENCE) // 最高优先级，最先执行
@Slf4j
public class RequestLoggingFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String requestId = generateRequestId();
        MDC.put(ThreadMdcUtils.getTraceId(), requestId);
        MDC.put("username", "anonymous");
        long startTime = System.currentTimeMillis();

        try {
            // 记录请求信息
            log.info("Request: {} {}",
                    request.getMethod(), request.getRequestURI());

            // 包装Response以记录状态码
            ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
            chain.doFilter(request, responseWrapper);

            long duration = System.currentTimeMillis() - startTime;
            log.info("Request Completed: {} {} - 状态: {} - 耗时: {}ms ←←←",
                    request.getMethod(), request.getRequestURI(),
                    responseWrapper.getStatus(), duration);
            responseWrapper.copyBodyToResponse();
        } finally {
            MDC.clear();
        }
    }

    // 生成请求ID的方法
    private String generateRequestId() {
        return UUID.randomUUID().toString();
    }
}
