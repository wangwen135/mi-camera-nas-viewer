package com.wwh.camera;

import com.wwh.camera.util.PasswordEncoderUtil;
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
        // 检测 --encrypt 参数
        if (args.length > 0 && args[0].equals("--encrypt")) {
            runPasswordEncryptor(args);
            return;
        }
        SpringApplication.run(MiCameraNasViewerApplication.class, args);
    }

    /**
     * 运行密码加密工具
     */
    private static void runPasswordEncryptor(String[] args) {
        System.out.println("===============================================");
        System.out.println("       小米摄像头 NAS 视频查看工具");
        System.out.println("           密码加密工具");
        System.out.println("===============================================");
        System.out.println();

        if (args.length > 1) {
            // 命令行参数模式
            String password = String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length));
            String encrypted = PasswordEncoderUtil.encrypt(password);
            System.out.println("明文密码: " + password);
            System.out.println("加密密文: " + encrypted);
            System.out.println();
            System.out.println("请将以上「加密密文」复制到 application.yml 的 security.password 配置项中");
        } else {
            // 交互模式
            System.out.println("请输入要加密的密码（输入后按回车确认）：");

            char[] passwordChars = System.console().readPassword();
            if (passwordChars == null) {
                System.err.println("读取密码失败，请确保在支持的控制台中运行");
                System.exit(1);
            }

            String password = new String(passwordChars);
            String encrypted = PasswordEncoderUtil.encrypt(password);

            System.out.println();
            System.out.println("===============================================");
            System.out.println("加密结果：");
            System.out.println("===============================================");
            System.out.println("明文密码: " + password);
            System.out.println("加密密文: " + encrypted);
            System.out.println();
            System.out.println("请将以上「加密密文」复制到 application.yml 的 security.password 配置项中");
            System.out.println("===============================================");

            java.util.Arrays.fill(passwordChars, '\0');
        }
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
