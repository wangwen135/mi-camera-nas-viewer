package com.wwh.camera;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 小米摄像头 NAS 视频查看工具启动类
 *
 * @author wangwh
 * @version 1.0
 */
@SpringBootApplication
public class MiCameraNasViewerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MiCameraNasViewerApplication.class, args);
        System.out.println("==== 小米摄像头 NAS 视频查看工具已启动 ====");
    }
}
