package com.spark.bitrade.entity;

import lombok.Data;

@Data
public class AppItem {
    private Long appId;
    private String appLabel;
    private String packageName;
    private String versionName;
    private Integer versionCode;
    private Long appSize;
    private String appFile;
    private String iconFile;
    private String remark;
}
