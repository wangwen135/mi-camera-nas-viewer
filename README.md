# 小米摄像头 NAS 视频查看工具

## 项目简介

这是一个用于查看存储在 NAS 服务器上的小米摄像头录像的 Web 应用工具。

## 功能特性

- 摄像头列表配置（通过配置文件）
- 按日期/小时/分钟浏览视频
- 视频在线播放、下载
- 播放速度控制、逐帧播放、画面缩放
- 密码保护、IP 锁定机制

## 快速开始

### 1. 修改配置

编辑 `src/main/resources/application.yml` 文件，配置以下内容：

```yaml
# 摄像头视频配置
camera:
  # 视频文件存储基础路径（NAS 路径）
  base-path: H:\监控\xiaomi_camera_videos

  # 摄像头列表配置
  cameras:
    - name: 门口摄像头
      code: 303abc123456
      enabled: true
    - name: 客厅摄像头
      code: 1bc487654321
      enabled: true

# 安全配置
security:
  # 访问密码（请修改为强密码）
  password: admin123
  # Session 过期时间（秒），默认30分钟
  session-timeout: 1800
  # 最大登录失败次数
  max-fail-count: 3
  # IP 锁定时间（秒），默认10分钟
  lock-time: 600
```

```yaml
# 日志配置
logging:
  level:
    com.wwh.camera: DEBUG      # 应用代码日志级别
    org.springframework.web: INFO  # 框架日志级别
  file:
    name: ./log/mi-camera-nas-viewer.log  # 日志文件路径
  logback:
    rollingpolicy:
      max-file-size: 100MB     # 单个文件最大大小
      max-history: 30          # 日志保留天数
      file-name-pattern: ./log/mi-camera-nas-viewer.%d{yyyy-MM-dd}.%i.log
```


**⚠️ 安全提示**：
- 首次使用请务必修改默认密码
- 建议使用强密码（包含大小写字母、数字、特殊字符）
- 定期更换密码以提高安全性

### 2. 编译运行

```bash
mvn clean package
java -jar target/mi-camera-nas-viewer-1.0.0.jar
```

或在 IDE 中运行 `MiCameraNasViewerApplication` 主类。

### 3. 访问应用

浏览器访问 `http://localhost:8866`，输入密码登录即可使用。

## 视频文件结构

```
H:\监控\xiaomi_camera_videos\
├── 303abc123456\
│   ├── 2024121817\
│   │   ├── 00M18S_1768057218.mp4
│   │   └── 01M18S_1768057278.mp4
│   └── 2024121818\
└── 1bc487654321\
```

摄像头代码可在 NAS 存储目录中找到，格式为 `303abc123456`、`607XXXXXXXXX` 等。

## 注意事项

- 确保 NAS 路径有正确的读取权限
- 视频文件名格式：`{分钟}M{秒}S_{时间戳}.mp4`，例如 `00M18S_1768057218.mp4`
- H.265 编码视频需要较新浏览器支持
- 首次使用请务必修改默认密码
- 建议使用 Chrome、Edge、Firefox 等现代浏览器
