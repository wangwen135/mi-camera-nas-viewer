package com.wwh.camera.service.impl;

import com.wwh.camera.config.CameraProperties;
import com.wwh.camera.entity.CameraConfig;
import com.wwh.camera.service.CameraVideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 摄像头视频服务实现
 *
 * @author wangwh
 * @version 1.0
 * @date 2022/11/28 21:13
 */
@Service
public class CameraVideoServiceImpl implements CameraVideoService {

    @Autowired
    private CameraProperties cameraProperties;

    @Override
    public List<CameraConfig> getCameraConfig() {
        return cameraProperties.getCameras().stream()
                .filter(item -> item.getEnabled() != null && item.getEnabled())
                .map(item -> {
                    CameraConfig config = new CameraConfig();
                    config.setName(item.getName());
                    config.setCode(item.getCode());
                    config.setEnabled(item.getEnabled());
                    return config;
                })
                .collect(Collectors.toList());
    }
}
