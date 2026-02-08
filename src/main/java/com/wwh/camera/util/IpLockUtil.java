package com.wwh.camera.util;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * IP 锁定工具类
 *
 * @author wangwh
 */
@Slf4j
public class IpLockUtil {

    /**
     * IP 失败次数记录
     */
    private static final Map<String, IpLockInfo> FAIL_COUNT_MAP = new ConcurrentHashMap<>();

    /**
     * IP 锁定信息
     */
    private static class IpLockInfo {
        /**
         * 失败次数
         */
        AtomicInteger failCount;
        /**
         * 锁定开始时间
         */
        Long lockStartTime;
        /**
         * 锁定结束时间
         */
        Long lockEndTime;

        IpLockInfo() {
            this.failCount = new AtomicInteger(0);
            this.lockStartTime = null;
            this.lockEndTime = null;
        }
    }

    /**
     * 检查 IP 是否被锁定
     *
     * @param ip       IP 地址
     * @param lockTime 锁定时长（秒）
     * @return true=已锁定，false=未锁定
     */
    public static boolean isLocked(String ip, int lockTime) {
        IpLockInfo lockInfo = FAIL_COUNT_MAP.get(ip);
        if (lockInfo == null) {
            return false;
        }

        // 检查是否在锁定期内
        if (lockInfo.lockEndTime != null && System.currentTimeMillis() < lockInfo.lockEndTime) {
            long remainingSeconds = (lockInfo.lockEndTime - System.currentTimeMillis()) / 1000;
            log.info("IP {} 已被锁定，剩余锁定时间：{} 秒", ip, remainingSeconds);
            return true;
        }

        // 锁定期已过，重置失败次数
        if (lockInfo.lockEndTime != null && System.currentTimeMillis() >= lockInfo.lockEndTime) {
            reset(ip);
        }

        return false;
    }

    /**
     * 记录登录失败
     *
     * @param ip          IP 地址
     * @param maxFailCount 最大失败次数
     * @param lockTime    锁定时长（秒）
     * @return 是否达到最大失败次数（true=已锁定）
     */
    public static boolean recordFail(String ip, int maxFailCount, int lockTime) {
        IpLockInfo lockInfo = FAIL_COUNT_MAP.computeIfAbsent(ip, k -> new IpLockInfo());
        int count = lockInfo.failCount.incrementAndGet();
        log.warn("IP {} 登录失败，当前失败次数：{}", ip, count);

        // 达到最大失败次数，锁定 IP
        if (count >= maxFailCount) {
            long now = System.currentTimeMillis();
            lockInfo.lockStartTime = now;
            lockInfo.lockEndTime = now + lockTime * 1000L;
            log.warn("IP {} 已达到最大失败次数 {}，锁定 {} 秒", ip, maxFailCount, lockTime);
            return true;
        }

        return false;
    }

    /**
     * 记录登录成功，重置失败次数
     *
     * @param ip IP 地址
     */
    public static void recordSuccess(String ip) {
        IpLockInfo lockInfo = FAIL_COUNT_MAP.get(ip);
        if (lockInfo != null) {
            int count = lockInfo.failCount.get();
            if (count > 0) {
                log.info("IP {} 登录成功，重置失败次数", ip);
            }
            reset(ip);
        }
    }

    /**
     * 获取剩余失败次数
     *
     * @param ip          IP 地址
     * @param maxFailCount 最大失败次数
     * @return 剩余失败次数
     */
    public static int getRemainingAttempts(String ip, int maxFailCount) {
        IpLockInfo lockInfo = FAIL_COUNT_MAP.get(ip);
        if (lockInfo == null) {
            return maxFailCount;
        }
        int currentFailCount = lockInfo.failCount.get();
        return Math.max(0, maxFailCount - currentFailCount);
    }

    /**
     * 获取剩余锁定时间（秒）
     *
     * @param ip IP 地址
     * @return 剩余锁定时间，未锁定返回 0
     */
    public static long getRemainingLockTime(String ip) {
        IpLockInfo lockInfo = FAIL_COUNT_MAP.get(ip);
        if (lockInfo == null || lockInfo.lockEndTime == null) {
            return 0;
        }
        long remaining = lockInfo.lockEndTime - System.currentTimeMillis();
        return Math.max(0, remaining / 1000);
    }

    /**
     * 重置 IP 失败记录
     *
     * @param ip IP 地址
     */
    public static void reset(String ip) {
        FAIL_COUNT_MAP.remove(ip);
    }

    /**
     * 清理过期的锁定记录（可定时调用）
     */
    public static void cleanExpired() {
        long now = System.currentTimeMillis();
        FAIL_COUNT_MAP.entrySet().removeIf(entry -> {
            IpLockInfo lockInfo = entry.getValue();
            return lockInfo.lockEndTime != null && now >= lockInfo.lockEndTime;
        });
    }
}
