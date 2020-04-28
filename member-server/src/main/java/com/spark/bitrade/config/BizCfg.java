package com.spark.bitrade.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.ToString;

@Component
@ConfigurationProperties(prefix = "biz")
@Data
@ToString(includeFieldNames=true)
public class BizCfg {

	private int distributeLevel;
}
