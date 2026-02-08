package com.wwh.camera.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 摄像头配置属性
 *
 * @author wangwh
 */
@Data
@Component
@ConfigurationProperties(prefix = "camera")
public class CameraProperties {

    /**
     * 视频文件存储基础路径
     */
    private String basePath;

    /**
     * 摄像头列表
     */
    private List<CameraItem> cameras = new ArrayList<>();

    @Data
    public static class CameraItem {
        private String name;
        private String code;
        private Boolean enabled = true;
    }
}
