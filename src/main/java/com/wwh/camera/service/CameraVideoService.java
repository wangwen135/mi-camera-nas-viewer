package com.wwh.camera.service;

import com.wwh.camera.entity.CameraConfig;
import org.springframework.core.io.FileSystemResource;

import java.io.File;
import java.util.List;

/**
 * 摄像头视频相关
 *
 * @author wangwh
 * @date 2022/11/28
 */
public interface CameraVideoService {

    /**
     * 获取已启用的摄像头配置列表
     */
    List<CameraConfig> getCameraConfig();

    /**
     * 获取指定摄像头有视频的日期列表
     *
     * @param cameraCode 摄像头代码
     * @return 日期列表 (yyyyMMdd 格式)
     */
    List<String> getVideoDates(String cameraCode);

    /**
     * 获取指定摄像头、指定日期有视频的小时列表
     *
     * @param cameraCode 摄像头代码
     * @param date       日期 (yyyyMMdd 格式)
     * @return 小时列表 (HH 格式)
     */
    List<String> getVideoHours(String cameraCode, String date);

    /**
     * 获取指定摄像头、指定日期、指定小时有视频的分钟列表
     *
     * @param cameraCode 摄像头代码
     * @param date       日期 (yyyyMMdd 格式)
     * @param hour       小时 (HH 格式)
     * @return 分钟列表 (mm 格式)
     */
    List<String> getVideoMinutes(String cameraCode, String date, String hour);

    /**
     * 获取指定时间的视频文件
     *
     * @param cameraCode 摄像头代码
     * @param date       日期 (yyyyMMdd 格式)
     * @param hour       小时 (HH 格式)
     * @param minute     分钟 (mm 格式)
     * @return 视频文件
     * @throws java.io.FileNotFoundException 视频文件不存在时抛出
     */
    File getVideoFile(String cameraCode, String date, String hour, String minute) throws java.io.FileNotFoundException;
}
