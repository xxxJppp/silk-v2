package com.spark.bitrade.core;

import com.baomidou.mybatisplus.core.enums.IEnum;

import java.io.Serializable;

/**
 * @author Zhang Jinwei
 * @date 2017年12月12日
 *
 * 修改：
 *   2019-01-23  yangch ：接口继承 IEnum接口 并默认是getValue方法，对mybatis-plus的枚举进行支持
 */
public interface BaseEnum extends IEnum {

    /**
     * 得到枚举类的序号
     *
     * @return 序号
     */
    int getOrdinal();

    @Override
    default Serializable getValue(){
        return getOrdinal();
    }
}
