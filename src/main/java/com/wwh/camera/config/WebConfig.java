package com.wwh.camera.config;

import com.wwh.camera.interceptor.AuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web 配置类
 *
 * @author wangwh
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**")  // 拦截所有路径
                .excludePathPatterns(
                        "/auth/login",           // 登录页面
                        "/auth/logout",          // 退出接口
                        "/login.html",           // 登录页面静态资源
                        "/error",                // 错误页面
                        "/bootstrap/**",         // Bootstrap 静态资源（CSS、JS、字体）
                        "/js/**",                // JavaScript 文件
                        "/images/**",            // 图片文件
                        "/css/**",               // CSS 文件
                        "/favicon.ico",          // 图标文件
                        "/favicon.svg"           // SVG 图标
                );
    }
}
