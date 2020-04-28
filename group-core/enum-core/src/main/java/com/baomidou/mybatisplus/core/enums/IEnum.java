package com.baomidou.mybatisplus.core.enums;

import java.io.Serializable;

/**
 * copy mybatis-plus 枚举接口
 * @param <T>
 */
public interface IEnum<T extends Serializable> {
    T getValue();
}
