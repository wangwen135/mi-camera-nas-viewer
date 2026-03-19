package com.wwh.camera.standalone;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.Console;

/**
 * 独立的密码加密工具
 * <p>
 * 可通过 java -jar 直接运行，无需 Spring 容器
 *
 * @author wangwh
 */
public class PasswordEncryptor {

    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();

    public static void main(String[] args) {
        System.out.println("===============================================");
        System.out.println("       小米摄像头 NAS 视频查看工具");
        System.out.println("           密码加密工具");
        System.out.println("===============================================");
        System.out.println();

        if (args.length > 0) {
            String password = String.join(" ", args);
            String encrypted = ENCODER.encode(password);
            System.out.println("明文密码: " + password);
            System.out.println("加密密文: " + encrypted);
            System.out.println();
            System.out.println("请将以上「加密密文」复制到 application.yml 的 security.password 配置项中");
        } else {
            Console console = System.console();
            if (console == null) {
                System.out.println("错误：请在支持控制台的环境中运行");
                System.out.println();
                System.out.println("或者使用命令行参数：");
                System.out.println("  java -jar password-encryptor.jar \"你的密码\"");
                System.exit(1);
            }

            System.out.println("请输入要加密的密码（输入后按回车确认）：");
            char[] passwordChars = console.readPassword();
            if (passwordChars == null) {
                System.err.println("读取密码失败");
                System.exit(1);
            }

            String password = new String(passwordChars);
            String encrypted = ENCODER.encode(password);

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
}
