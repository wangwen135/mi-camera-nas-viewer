package com.wwh.camera;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;

import java.net.InetAddress;

@SpringBootApplication
public class MiCameraNasViewerApplication {

    private final Environment environment;

    public MiCameraNasViewerApplication(Environment environment) {
        this.environment = environment;
    }

    public static void main(String[] args) {
        SpringApplication.run(MiCameraNasViewerApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        String port = environment.getProperty("server.port", "8080");
        String contextPath = environment.getProperty("server.servlet.context-path", "");
        String hostAddress = "localhost";

        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            hostAddress = "localhost";
        }

        String basePath = environment.getProperty("camera.base-path", "未配置");

        System.out.println();
        System.out.println("═════════════════════════════════════════════════════════════");
        System.out.println("               小米摄像头 NAS 视频查看工具已启动                  ");
        System.out.println("═════════════════════════════════════════════════════════════");
        System.out.println("  端口号:   " + port);
        System.out.println("  本地访问: " + "http://localhost:" + port + contextPath);
        System.out.println("  网络访问: " + "http://" + hostAddress + ":" + port + contextPath);
        System.out.println("  视频路径: " + basePath);
        System.out.println("═════════════════════════════════════════════════════════════");
        System.out.println();
    }
}
