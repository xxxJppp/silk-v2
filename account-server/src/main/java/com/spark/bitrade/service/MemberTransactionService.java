package com.spark.bitrade.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.constant.TransactionType;
import com.spark.bitrade.entity.MemberTransaction;
import com.spark.bitrade.vo.WidthRechargeStaticsVo;

import java.util.Date;

/**
 * (MemberTransaction)表服务接口
 *
 * @author yangch
 * @since 2019-06-15 16:27:30
 */
public interface MemberTransactionService extends IService<MemberTransaction> {

    IPage<WidthRechargeStaticsVo> widthRechargeStaticsVo(TransactionType type, Date startTime, Date endTime, String coin, IPage page);

}