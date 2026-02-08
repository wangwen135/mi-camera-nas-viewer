package com.wwh.camera.entity;

import lombok.Data;

/**
 * 摄像头配置
 *
 * @author wangwh
 * @version 1.0
 * @date 2022/11/28 21:09
 */
@Data
public class CameraConfig {
    private String name;
    private String code;
    private Boolean enabled;
}
