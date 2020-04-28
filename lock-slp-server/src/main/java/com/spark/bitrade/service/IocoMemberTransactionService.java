package com.spark.bitrade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.entity.IocoMemberTransaction;
import com.spark.bitrade.entity.IocoMemberWallet;
import com.spark.bitrade.vo.IocoPurchaseTransactionVo;
import com.spark.bitrade.vo.IocoPurchaseVo;

import java.math.BigDecimal;

/**
 * ioco钱包交易记录(IocoMemberTransaction)表服务接口
 *
 * @author daring5920
 * @since 2019-07-03 14:38:58
 */
public interface IocoMemberTransactionService extends IService<IocoMemberTransaction> {

    /**
     * 获取ioco首页数据
     * @author shenzucai
     * @time 2019.07.03 17:04
     * @param memberId
     * @return true
     */
    IocoPurchaseVo getIocoIndexData(Long memberId);

    /**
     * 获取ioco转赠界面数据
     * @author shenzucai
     * @time 2019.07.03 17:04
     * @param memberId
     * @return true
     */
    IocoMemberWallet giftIndex(Long memberId);


    /**
     * 申购接口
     * @author shenzucai
     * @time 2019.07.03 21:50
     * @param memberId 用户id
     * @param purchasetUnit 支付币种
     * @param purchaseAmount 支付数量
     * @param slpAmount 申购数量
     * @param share 申购份数
     * @return true
     */
    Boolean purchaseSLP(Long memberId, String purchasetUnit, BigDecimal purchaseAmount,BigDecimal slpAmount,Integer share,Long activityId);


    /**
     * 转赠接口
     * @author shenzucai
     * @time 2019.07.03 21:50
     * @param memberId 用户id
     * @param giftUnit 赠送币种
     * @param giftAmount 赠送数量
     * @param giftTo 赠送对象
     * @return true
     */
    Boolean giftSLP(Long memberId, String giftUnit,BigDecimal giftAmount,String giftTo);


    /**
     * ioco转账记录
     * @author shenzucai
     * @time 2019.07.04 8:40
     * @param memberId 用户id
     * @param size 分页大小
     * @param current 当前页码
     * @param type 转账类型
     * @return true
     */
    IPage<IocoPurchaseTransactionVo> listByType(Long memberId,Integer size, Integer current, Integer type);

}