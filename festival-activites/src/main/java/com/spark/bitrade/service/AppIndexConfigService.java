package com.spark.bitrade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.entity.AppIndexConfig;
import com.spark.bitrade.vo.AppIndexConfigVo;

import java.util.List;

/**
 * <p>
 * app首页快捷入口 服务类
 * </p>
 *
 * @author qiliao
 * @since 2020-01-02
 */
public interface AppIndexConfigService extends IService<AppIndexConfig> {

    List<AppIndexConfigVo> appIndexList(String language);

}
