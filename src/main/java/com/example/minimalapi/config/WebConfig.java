package com.example.minimalapi.config;

import com.example.minimalapi.logging.RequestLifecycleInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final RequestLifecycleInterceptor requestLifecycleInterceptor;

    public WebConfig(RequestLifecycleInterceptor requestLifecycleInterceptor) {
        this.requestLifecycleInterceptor = requestLifecycleInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestLifecycleInterceptor);
    }
}
