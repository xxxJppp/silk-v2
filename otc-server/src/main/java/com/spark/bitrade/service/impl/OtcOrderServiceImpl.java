package com.spark.bitrade.service.impl;

import com.spark.bitrade.entity.OtcOrder;
import com.spark.bitrade.enums.AdvertiseType;
import com.spark.bitrade.mapper.OtcOrderMapper;
import com.spark.bitrade.service.OtcOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * (OtcOrder)表服务实现类
 *
 * @author ss
 * @date 2020-03-19 10:23:51
 */
@Service("otcOrderService")
public class OtcOrderServiceImpl extends ServiceImpl<OtcOrderMapper,OtcOrder> implements OtcOrderService {
    @Resource
    private OtcOrderMapper otcOrderMapper;


    @Override
    public List<Map<String, Long>> selectCountByMembers(Long[] memberIds, AdvertiseType type) {
        return otcOrderMapper.findCountByMembers(memberIds, type == AdvertiseType.BUY ? 0 : 1);
    }

    @Override
    public List<Map<String, Long>> selectCountByMembersAnd48(Long[] memberIds, AdvertiseType type, Date date) {
        return otcOrderMapper.selectCountByMembersAnd48(memberIds, type == AdvertiseType.BUY ? 0 : 1, date);
    }

    @Override
    public boolean isAllowTrade(Long customerId, Integer limitNum) {
        boolean isAllowTrade = false;
        int unFinishNum = otcOrderMapper.findUnFinishNum(customerId);
        if (unFinishNum < limitNum) {
            isAllowTrade = true;
        }
        return isAllowTrade;
    }



    /**
     * 允许小数最后一位的精度有“正负1”的误差
     * @param v1
     * @param v2
     * @return
     */
    @Override
    public boolean isEqualIgnoreTailPrecision(BigDecimal v1, BigDecimal v2) {
        if (v1.compareTo(v2) == 0) {
            return true;
        } else {
            //最大精度位数
            int maxScala = Math.max(v1.scale(), v2.scale());

            //精度
            BigDecimal ignorePrecision = BigDecimal.ONE
                    .divide(BigDecimal.valueOf(Math.pow(10, maxScala)));

            if (v1.subtract(v2).abs()
                    .compareTo(ignorePrecision) == 0) {
                return true;
            }
        }

        return false;
    }


}
