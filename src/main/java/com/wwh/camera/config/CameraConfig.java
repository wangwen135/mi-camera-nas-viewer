package com.wwh.camera.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 摄像头配置类
 *
 * @author wangwh
 */
@Configuration
@EnableConfigurationProperties({CameraProperties.class, SecurityProperties.class})
public class CameraConfig {
}
