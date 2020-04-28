package com.spark.bitrade.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 地址
 *
 * @author Zhang Jinwei
 * @date 2018年01月02日
 */
@Data
public class Location implements Serializable {
    private String country;
    private String province;
    private String city;
    private String district;

    public Location convert(Member member) {
        setCountry(member.getCountry());
        setProvince(member.getProvince());
        setCity(member.getCity());
        setDistrict(member.getDistrict());
        return this;
    }
}
