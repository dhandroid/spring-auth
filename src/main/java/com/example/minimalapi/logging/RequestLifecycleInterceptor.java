package com.example.minimalapi.logging;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RequestLifecycleInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(RequestLifecycleInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        log.info("[Interceptor-preHandle] handler={} path={}",
                handler.getClass().getSimpleName(), request.getRequestURI());
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           org.springframework.web.servlet.ModelAndView modelAndView) {
        log.info("[Interceptor-postHandle] path={} status={}", request.getRequestURI(), response.getStatus());
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                Exception ex) {
        if (ex == null) {
            log.info("[Interceptor-afterCompletion] path={} completed successfully", request.getRequestURI());
        } else {
            log.error("[Interceptor-afterCompletion] path={} completed with exception",
                    request.getRequestURI(), ex);
        }
    }
}
