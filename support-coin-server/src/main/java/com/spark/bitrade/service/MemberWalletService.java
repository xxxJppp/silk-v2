package com.spark.bitrade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.entity.SupportNewsInfo;
import com.spark.bitrade.vo.IocoPurchaseVo;

/**
 * <p>
 *   账户操作
 * </p>
 *
 * @author lc
 * @since 2019-11-05
 */
public interface MemberWalletService {


    /**
     * 获取ioco首页数据
     * @author shenzucai
     * @time 2019.07.03 17:04
     * @param memberId
     * @return true
     */
    IocoPurchaseVo getIocoIndexData(Long memberId);

}
