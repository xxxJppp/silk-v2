package com.spark.bitrade.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author wsy
 * @since 2019/7/18 14:26
 */
@Data
public class MqttAppVersion {
    private Date currentTime;             // 当前时间
    private String hookApkPackage;        // Hook程序包名
    private String hookApkVersion;        // Hook程序版本
    private String hookApkDownload;       // Hook程序下载地址
    private String scriptApkPackage;      // 脚本APP包名
    private String scriptApkVersion;      // 脚本APP版本
    private String scriptApkDownload;     // 脚本APP下载地址
    private String scriptJarMain;         // 脚本Jar入口
    private String scriptJarVersion;      // 脚本Jar版本
    private String scriptJarDownload;     // 脚本Jar下载地址
}
