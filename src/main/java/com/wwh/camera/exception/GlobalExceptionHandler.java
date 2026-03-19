package com.wwh.camera.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常处理器
 *
 * @author wangwh
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理所有未捕获的异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception ex, HttpServletRequest request) {
        log.error("发生未捕获的异常，请求路径：{}", request.getRequestURI(), ex);

        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("message", "服务器内部错误，请稍后重试");
        result.put("error", ex.getClass().getSimpleName());

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(result);
    }

    /**
     * 处理参数非法异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        log.warn("参数非法，请求路径：{}，错误信息：{}", request.getRequestURI(), ex.getMessage());

        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("message", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(result);
    }

    /**
     * 处理文件未找到异常
     */
    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleFileNotFound(FileNotFoundException ex, HttpServletRequest request) {
        log.warn("文件未找到，请求路径：{}，错误信息：{}", request.getRequestURI(), ex.getMessage());

        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("message", "视频文件不存在");

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(result);
    }

    /**
     * 处理 404 异常
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(NoHandlerFoundException ex, HttpServletRequest request) {
        log.warn("请求路径不存在：{}", request.getRequestURI());

        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("message", "请求的资源不存在");

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(result);
    }

    /**
     * 处理运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntime(RuntimeException ex, HttpServletRequest request) {
        log.error("运行时异常，请求路径：{}", request.getRequestURI(), ex);

        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("message", "请求处理失败：" + ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(result);
    }
}
