package com.spark.bitrade.biz;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.spark.bitrade.entity.MemberRuleDescr;
import com.spark.bitrade.param.PageParam;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: Zhong Jiang
 * @time: 2019.11.18 18:04
 */

public interface IRuleDescrService {

    /**
     * 查询会员规则
     *
     * @return
     */
    List<MemberRuleDescr> getMemberRuleDescr();
}
