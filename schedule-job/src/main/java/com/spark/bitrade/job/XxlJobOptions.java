package com.spark.bitrade.job;

import com.xxl.job.core.executor.XxlJobExecutor;
import lombok.Data;

/**
 * XxlJobOptions
 *
 * @author Archx[archx@foxmail.com]
 * @since 2019/7/8 18:14
 */
@Data
public class XxlJobOptions {

    public static final String PREFIX = "xxl.job.executor";

    private String adminAddresses;
    private String appName;
    private String ip;
    private int port;
    private String accessToken;
    private String logPath;
    private int logRetentionDays;

    public void setup(XxlJobExecutor executor) {
        executor.setAdminAddresses(adminAddresses);
        executor.setAppName(appName);
        executor.setIp(ip);
        executor.setPort(port);
        executor.setAccessToken(accessToken);
        executor.setLogPath(logPath);
        executor.setLogRetentionDays(logRetentionDays);
    }

}
