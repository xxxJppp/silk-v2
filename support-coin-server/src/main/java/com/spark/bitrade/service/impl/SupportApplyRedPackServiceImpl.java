package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.entity.SupportApplyRedPack;
import com.spark.bitrade.mapper.SupportApplyRedPackMapper;
import com.spark.bitrade.param.PageParam;
import com.spark.bitrade.param.RedPackParam;
import com.spark.bitrade.param.RedRecieveParam;
import com.spark.bitrade.service.SupportApplyRedPackService;
import com.spark.bitrade.vo.ApplyRedPackAuditRecordVo;
import com.spark.bitrade.vo.ApplyRedPackListVo;
import com.spark.bitrade.vo.RedPackRecieveDetailVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

/**
 * <p>
 * 红包申请表 服务实现类
 * </p>
 *
 * @author qhliao
 * @since 2020-02-19
 */
@Service
public class SupportApplyRedPackServiceImpl extends ServiceImpl<SupportApplyRedPackMapper, SupportApplyRedPack> implements SupportApplyRedPackService {

    @Override
    public List<ApplyRedPackListVo> applyRedPackList(IPage<ApplyRedPackListVo> page, RedPackParam param, String coin) {
        List<ApplyRedPackListVo> applyRedPackListVos = baseMapper.applyRedPackList(page, param, coin);
        for (ApplyRedPackListVo vo:applyRedPackListVos){
            BigDecimal redTotalAmount = vo.getRedTotalAmount();
            //查询已领取红包
            BigDecimal receivedPack = baseMapper.findReceivedPack(vo.getRedPackManageId());
            BigDecimal bigDecimal = Optional.ofNullable(receivedPack).orElse(BigDecimal.ZERO);
            String s = bigDecimal.divide(redTotalAmount, 4, RoundingMode.UP).multiply(new BigDecimal(100)).setScale(2,RoundingMode.UP).toPlainString();
            vo.setReceiveProgress(s+"%");
        }
        return applyRedPackListVos;
    }

    @Override
    public List<RedPackRecieveDetailVo> applyRedPackStatics(IPage<RedPackRecieveDetailVo> page, RedRecieveParam param, Long applyRedPackId) {
        return baseMapper.applyRedPackStatics(page,param,applyRedPackId);
    }

    public Integer findNewMemberCount(  Long applyRedPackId ){
        return baseMapper.findNewMemberCount(applyRedPackId);
    }
}
