package com.wwh.camera.service;

import com.wwh.camera.entity.CameraConfig;

import java.util.List;

/**
 * 摄像头视频相关
 *
 * @author wangwh
 * @date 2022/11/28
 */
public interface CameraVideoService {
    List<CameraConfig> getCameraConfig();
}
