package com.spark.bitrade.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author: Zhong Jiang
 * @date: 2019-11-21 14:54
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PageMemberVo {

    private long total;

    private long size;

    private long current;

    List<MemberVo> list = new ArrayList<>();
}
