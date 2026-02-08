package com.wwh.camera.controller;

import com.wwh.camera.config.CameraProperties;
import com.wwh.camera.entity.CameraConfig;
import com.wwh.camera.service.CameraVideoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.io.File;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 摄像头视频相关
 *
 * @author wangwh
 * @date 2022/11/28
 */
@Slf4j
@RestController
@RequestMapping("/camera")
public class CameraVideoController {

    @Autowired
    private CameraVideoService cameraVideoService;

    @Autowired
    private CameraProperties cameraProperties;

    @GetMapping("/video/path")
    public String getVideoBasePath() {
        return cameraProperties.getBasePath();
    }

    @GetMapping("/list")
    public List<Map<String, String>> list() {
        List<CameraConfig> list = cameraVideoService.getCameraConfig();

        return list.stream().map(cc -> {
            Map<String, String> m = new HashMap<>();
            m.put("name", cc.getName());
            m.put("code", cc.getCode());
            return m;
        }).collect(Collectors.toList());
    }

    /**
     * 获取摄像头有视频的日期
     *
     * @param code 摄像头代码
     * @return 日期列表
     */
    @GetMapping("/video/date")
    public List<String> getVideoDate(@RequestParam String code) {
        File baseDir = new File(cameraProperties.getBasePath());
        File videoDir = new File(baseDir, code);
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

    /**
     * 获取某个摄像头某个日期下面有视频的小时
     *
     * @param code 摄像头代码
     * @param date 日期（yyyyMMdd）
     * @return 小时列表
     */
    @GetMapping("/video/hour")
    public List<String> getVideoHour(@RequestParam String code, @RequestParam String date) {
        File baseDir = new File(cameraProperties.getBasePath());
        File videoDir = new File(baseDir, code);
        String[] dayHours = videoDir.list((dir, name) -> name.startsWith(date));
        if (dayHours == null) {
            return Collections.emptyList();
        }
        return Arrays.stream(dayHours)
                .map(x -> x.substring(8))
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * 获取某个摄像头某个日期下面某小时有视频的分钟
     *
     * @param code 摄像头代码
     * @param date 日期（yyyyMMdd）
     * @param hour 小时（HH）
     * @return 分钟列表
     */
    @GetMapping("/video/minute")
    public List<String> getVideoMinute(@RequestParam String code, @RequestParam String date, @RequestParam String hour) {
        File baseDir = new File(cameraProperties.getBasePath());
        File videoDir = new File(baseDir, code);
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

    /**
     * 获取视频
     *
     * @param code 摄像头代码
     * @param date 日期（yyyyMMdd）
     * @param hour 小时（HH）
     * @param minute 分钟（mm）
     * @param op 操作类型（download=下载）
     * @return 视频文件
     */
    @RequestMapping(value = "/video/{code}/{date}-{hour}-{minute}.mp4")
    public ResponseEntity video(WebRequest webRequest,
                               @PathVariable String code,
                               @PathVariable String date,
                               @PathVariable String hour,
                               @PathVariable String minute,
                               @RequestParam(required = false) String op) {

        log.debug("访问视频：摄像头={}, 时间={}-{}:{}", code, date, hour, minute);

        // 检查 ETag，支持 304 缓存
        String rEtag = webRequest.getHeader("If-None-Match");
        String etag = date + hour + minute;

        if (rEtag != null) {
            rEtag = rEtag.replace("\"", "");
            if (etag.equals(rEtag)) {
                log.debug("返回 304 NOT_MODIFIED");
                return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
            }
        }

        // 查找视频文件
        File baseDir = new File(cameraProperties.getBasePath());
        File videoDir = new File(baseDir, code);
        File ymdhDir = new File(videoDir, date + hour);
        File[] videoFile = ymdhDir.listFiles((dir, name) -> name.startsWith(minute));

        if (videoFile == null || videoFile.length == 0) {
            log.warn("视频文件没有找到，路径：{}，分钟：{}", ymdhDir.getAbsolutePath(), minute);
            return new ResponseEntity<>("video file not found", HttpStatus.NOT_FOUND);
        }

        // 每分钟只有一个文件
        File file = videoFile[0];
        String fileName = date + "-" + hour + "-" + minute + ".mp4";

        // 支持断点续传和缓存
        if ("download".equals(op)) {
            // 下载模式
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                    .body(new FileSystemResource(file));
        } else {
            // 播放模式
            return ResponseEntity.ok()
                    .contentType(MediaType.valueOf("video/mp4"))
                    .cacheControl(org.springframework.http.CacheControl
                            .maxAge(Duration.ofMinutes(5))
                            .noTransform()
                            .mustRevalidate()
                            .cachePrivate())
                    .eTag(etag)
                    .lastModified(file.lastModified())
                    .body(new FileSystemResource(file));
        }
    }
}
