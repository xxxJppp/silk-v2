package com.spark.bitrade.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author: Zhong Jiang
 * @date: 2019-11-21 14:54
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MemberVo {

    private Long id;

    private Date registrationTime;

    private Integer level;

    private String username;

    List<MemberVo> list;

}
