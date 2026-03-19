package com.wwh.camera.service.impl;

import com.wwh.camera.config.CameraProperties;
import com.wwh.camera.entity.CameraConfig;
import com.wwh.camera.service.CameraVideoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 摄像头视频服务实现
 *
 * @author wangwh
 * @version 1.0
 * @date 2022/11/28 21:13
 */
@Slf4j
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

    @Override
    public List<String> getVideoDates(String cameraCode) {
        File videoDir = getCameraVideoDir(cameraCode);
        String[] dayHours = videoDir.list();
        if (dayHours == null) {
            return Collections.emptyList();
        }
        return Arrays.stream(dayHours)
                .map(ymmddhh -> ymmddhh.substring(0, 8))
                .sorted()
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getVideoHours(String cameraCode, String date) {
        File videoDir = getCameraVideoDir(cameraCode);
        String[] dayHours = videoDir.list((dir, name) -> name.startsWith(date));
        if (dayHours == null) {
            return Collections.emptyList();
        }
        return Arrays.stream(dayHours)
                .map(x -> x.substring(8))
                .sorted()
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getVideoMinutes(String cameraCode, String date, String hour) {
        File videoDir = getCameraVideoDir(cameraCode);
        File ymdhDir = new File(videoDir, date + hour);
        String[] minutes = ymdhDir.list();
        if (minutes == null) {
            return Collections.emptyList();
        }
        return Arrays.stream(minutes)
                .map(x -> x.substring(0, 2))
                .sorted()
                .collect(Collectors.toList());
    }

    @Override
    public File getVideoFile(String cameraCode, String date, String hour, String minute) throws FileNotFoundException {
        File videoDir = getCameraVideoDir(cameraCode);
        File ymdhDir = new File(videoDir, date + hour);
        File[] videoFiles = ymdhDir.listFiles((dir, name) -> name.startsWith(minute));

        if (videoFiles == null || videoFiles.length == 0) {
            throw new FileNotFoundException("视频文件不存在：摄像头=" + cameraCode + "，时间=" + date + "-" + hour + ":" + minute);
        }

        return videoFiles[0];
    }

    /**
     * 获取摄像头视频目录
     *
     * @param cameraCode 摄像头代码
     * @return 视频目录
     * @throws IllegalArgumentException 摄像头代码为空时抛出
     */
    private File getCameraVideoDir(String cameraCode) {
        if (cameraCode == null || cameraCode.trim().isEmpty()) {
            throw new IllegalArgumentException("摄像头代码不能为空");
        }

        File baseDir = new File(cameraProperties.getBasePath());
        File videoDir = new File(baseDir, cameraCode);

        if (!videoDir.exists()) {
            log.warn("摄像头视频目录不存在：{}", videoDir.getAbsolutePath());
            return videoDir;
        }

        return videoDir;
    }
}
