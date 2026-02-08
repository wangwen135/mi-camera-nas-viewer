package com.wwh.camera.controller;

import com.wwh.camera.config.SecurityProperties;
import com.wwh.camera.util.IpLockUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * 登录控制器
 *
 * @author wangwh
 */
@Slf4j
@Controller
@RequestMapping("/auth")
public class LoginController {

    @Autowired
    private SecurityProperties securityProperties;

    private static final String SESSION_KEY = "loggedIn";

    /**
     * 登录页面
     */
    @GetMapping("/login")
    public String loginPage() {
        return "forward:/login.html";
    }

    /**
     * 登录接口
     */
    @PostMapping("/login")
    @ResponseBody
    public Map<String, Object> login(@RequestParam String password,
                                     @RequestParam(required = false) String captcha,
                                     HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        String ip = getClientIp(request);

        // 检查 IP 是否被锁定
        if (IpLockUtil.isLocked(ip, securityProperties.getLockTime())) {
            long remainingTime = IpLockUtil.getRemainingLockTime(ip);
            result.put("success", false);
            result.put("message", "IP已被锁定，请在 " + remainingTime + " 秒后重试");
            result.put("locked", true);
            result.put("remainingTime", remainingTime);
            return result;
        }

        // 验证密码
        if (!securityProperties.getPassword().equals(password)) {
            // 记录失败
            boolean locked = IpLockUtil.recordFail(ip, securityProperties.getMaxFailCount(), securityProperties.getLockTime());
            int remainingAttempts = IpLockUtil.getRemainingAttempts(ip, securityProperties.getMaxFailCount());

            result.put("success", false);

            if (locked) {
                long remainingTime = IpLockUtil.getRemainingLockTime(ip);
                result.put("message", "密码错误次数过多，IP已被锁定 " + remainingTime + " 秒");
                result.put("locked", true);
                result.put("remainingTime", remainingTime);
            } else {
                result.put("message", "密码错误，还剩 " + remainingAttempts + " 次机会");
                result.put("locked", false);
                result.put("remainingAttempts", remainingAttempts);
            }
            return result;
        }

        // 登录成功，记录到 session
        HttpSession session = request.getSession(true);
        session.setAttribute(SESSION_KEY, true);
        session.setMaxInactiveInterval(securityProperties.getSessionTimeout());

        // 重置失败次数
        IpLockUtil.recordSuccess(ip);

        log.info("用户登录成功，IP：{}", ip);

        result.put("success", true);
        result.put("message", "登录成功");
        return result;
    }

    /**
     * 检查登录状态
     */
    @GetMapping("/check")
    @ResponseBody
    public Map<String, Object> check(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession(false);
        boolean loggedIn = session != null && session.getAttribute(SESSION_KEY) != null;
        result.put("loggedIn", loggedIn);
        return result;
    }

    /**
     * 退出登录
     */
    @PostMapping("/logout")
    @ResponseBody
    public Map<String, Object> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "已退出登录");
        return result;
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
        // 处理多个 IP 的情况（X-Forwarded-For 可能包含多个 IP）
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
