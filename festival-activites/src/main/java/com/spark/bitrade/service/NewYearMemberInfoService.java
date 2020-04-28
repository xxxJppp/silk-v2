package com.spark.bitrade.service;

import com.spark.bitrade.entity.NewYearMemberInfo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户矿石表 服务类
 * </p>
 *
 * @author qiliao
 * @since 2019-12-30
 */
public interface NewYearMemberInfoService extends IService<NewYearMemberInfo> {

    NewYearMemberInfo findRecordByMemberId(Long memberId);

    /**
     * 用户挖矿次数减 1
     * @param memberId
     * @return
     */
    boolean decrMemberMiningNumber(Long memberId);

    /**
     * 用户挖矿次数加 1
     *
     * @param memberId
     * @return
     */
    boolean incrMemberMiningNumber(Long memberId);


}
