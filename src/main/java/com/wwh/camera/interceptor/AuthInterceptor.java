package com.wwh.camera.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wwh.camera.config.SecurityProperties;
import com.wwh.camera.util.IpLockUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * 认证拦截器
 *
 * @author wangwh
 */
@Slf4j
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private SecurityProperties securityProperties;

    private static final String SESSION_KEY = "loggedIn";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        String ip = getClientIp(request);

        log.debug("处理请求：{} 来自 {}", uri, ip);

        // 检查 IP 是否被锁定
        if (IpLockUtil.isLocked(ip, securityProperties.getLockTime())) {
            log.warn("拒绝访问：IP {} 已被锁定，请求路径：{}", ip, uri);
            if (isAjaxRequest(request)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json;charset=UTF-8");
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("message", "IP已被锁定，请稍后重试");
                result.put("locked", true);
                result.put("remainingTime", IpLockUtil.getRemainingLockTime(ip));
                response.getWriter().write(objectMapper.writeValueAsString(result));
            } else {
                response.sendRedirect("/auth/login?locked=true");
            }
            return false;
        }

        // 检查 session 是否已登录
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute(SESSION_KEY) != null) {
            return true;
        }

        // 未登录，拒绝访问
        log.info("拒绝访问：IP {} 未登录，请求路径：{}", ip, uri);
        if (isAjaxRequest(request)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "未登录");
            result.put("needLogin", true);
            response.getWriter().write(objectMapper.writeValueAsString(result));
        } else {
            response.sendRedirect("/auth/login");
        }
        return false;
    }

    /**
     * 判断是否为 AJAX 请求
     */
    private boolean isAjaxRequest(HttpServletRequest request) {
        String requestedWith = request.getHeader("X-Requested-With");
        return "XMLHttpRequest".equals(requestedWith) || request.getRequestURI().startsWith("/camera/");
    }

    /**
     * 获取客户端 IP 地址
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 处理多个 IP 的情况
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
