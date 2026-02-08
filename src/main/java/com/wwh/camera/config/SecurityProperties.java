package com.wwh.camera.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 安全配置属性
 *
 * @author wangwh
 */
@Data
@Component
@ConfigurationProperties(prefix = "security")
public class SecurityProperties {

    /**
     * 访问密码
     */
    private String password;

    /**
     * Session 过期时间（秒）
     */
    private Integer sessionTimeout = 1800;

    /**
     * 最大登录失败次数
     */
    private Integer maxFailCount = 3;

    /**
     * IP 锁定时间（秒）
     */
    private Integer lockTime = 600;
}
