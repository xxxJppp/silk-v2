package com.spark.bitrade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.entity.CywWalletSnapshoot;
import org.apache.ibatis.annotations.Param;

/**
 * 机器人钱包每日快照表(CywWalletSnapshoot)表数据库访问层
 *
 * @author yangch
 * @since 2019-09-25 19:09:43
 */
public interface CywWalletSnapshootMapper extends BaseMapper<CywWalletSnapshoot> {

    /**
     * 获取最新的数据
     *
     * @param memberId
     * @param coinUnit
     * @return
     */
    CywWalletSnapshoot getNewest(@Param("memberId") Long memberId, @Param("coinUnit") String coinUnit);

    /**
     * 删除指定周期的数据
     *
     * @param optime
     * @return
     */
    int deleteData(@Param("optime") int optime);
}