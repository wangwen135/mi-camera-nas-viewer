package com.wwh.camera.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码加密工具类
 * <p>
 * 使用 BCrypt 算法对密码进行加密，用户可以将加密后的密文配置到 application.yml 中
 * <p>
 * 使用方式：
 * <pre>
 * // 加密密码
 * String encryptedPassword = PasswordEncoderUtil.encrypt("myPassword");
 * // 验证密码
 * boolean matches = PasswordEncoderUtil.matches("myPassword", encryptedPassword);
 * </pre>
 *
 * @author wangwh
 */
public class PasswordEncoderUtil {

    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();

    /**
     * 加密密码
     *
     * @param rawPassword 明文密码
     * @return 加密后的密文（格式：$2a$10$...）
     */
    public static String encrypt(String rawPassword) {
        return ENCODER.encode(rawPassword);
    }

    /**
     * 验证密码是否匹配
     * <p>
     * 支持两种模式：
     * <ul>
     *   <li>密文模式：输入的明文与配置的密文进行 BCrypt 验证</li>
     *   <li>明文模式：直接进行字符串比较（兼容旧配置）</li>
     * </ul>
     *
     * @param rawPassword     明文密码（用户输入的）
     * @param encodedPassword 密文密码（配置文件中的，可能是明文也可能是密文）
     * @return true=密码匹配，false=密码不匹配
     */
    public static boolean matches(String rawPassword, String encodedPassword) {
        // 如果配置的是 BCrypt 密文（以 $2a$ 开头）
        if (encodedPassword != null && encodedPassword.startsWith("$2a$")) {
            return ENCODER.matches(rawPassword, encodedPassword);
        }
        // 兼容明文密码配置
        return rawPassword != null && rawPassword.equals(encodedPassword);
    }

    /**
     * 命令行入口，用于生成加密密码
     * <p>
     * 运行方式：
     * <pre>
     * java -cp mi-camera-nas-viewer.jar com.wwh.camera.util.PasswordEncoderUtil
     * </pre>
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        System.out.println("===============================================");
        System.out.println("       小米摄像头 NAS 视频查看工具");
        System.out.println("           密码加密工具");
        System.out.println("===============================================");
        System.out.println();

        if (args.length > 0) {
            // 命令行参数模式
            String password = String.join(" ", args);
            String encrypted = encrypt(password);
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
            String encrypted = encrypt(password);

            System.out.println();
            System.out.println("===============================================");
            System.out.println("加密结果：");
            System.out.println("===============================================");
            System.out.println("明文密码: " + password);
            System.out.println("加密密文: " + encrypted);
            System.out.println();
            System.out.println("请将以上「加密密文」复制到 application.yml 的 security.password 配置项中");
            System.out.println("===============================================");

            // 清空内存中的密码
            java.util.Arrays.fill(passwordChars, '\0');
        }
    }
}
