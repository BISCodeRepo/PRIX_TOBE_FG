package com.prix.homepage.backend.basic.authorization;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SessionLevelInterceptor())
                .addPathPatterns("/admin/**"); // /admin 패턴의 URL에만 Interceptor 적용
    }
}
