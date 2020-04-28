package com.spark.bitrade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.spark.bitrade.entity.MemberRuleDescr;
import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.param.PageParam;

import java.util.List;

/**
 * <p>
 * 会员规则 服务类
 * </p>
 *
 * @author Zhong Jiang
 * @since 2019-11-18
 */
public interface MemberRuleDescrService extends IService<MemberRuleDescr> {

    /**
     * 查询规则列表
     *
     * @return
     */
    List<MemberRuleDescr> getRuleDescrList();
}
