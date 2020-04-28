package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.constant.TransactionType;
import com.spark.bitrade.entity.MemberTransaction;
import com.spark.bitrade.mapper.MemberTransactionMapper;
import com.spark.bitrade.service.MemberTransactionService;
import com.spark.bitrade.vo.WidthRechargeStaticsVo;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * (MemberTransaction)表服务实现类
 *
 * @author yangch
 * @since 2019-06-15 16:27:30
 */
@Service("memberTransactionService")
public class MemberTransactionServiceImpl extends ServiceImpl<MemberTransactionMapper, MemberTransaction> implements MemberTransactionService {

    @Override
    public IPage<WidthRechargeStaticsVo> widthRechargeStaticsVo(TransactionType type, Date startTime, Date endTime, String coin, IPage page) {
        List<WidthRechargeStaticsVo> list=this.baseMapper.widthRechargeStaticsVo( type,startTime,endTime,coin,page);
        page.setRecords(list);
        return page;
    }
}