package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.entity.SupportOpenRedPack;
import com.spark.bitrade.mapper.SupportOpenRedPackMapper;
import com.spark.bitrade.service.SupportOpenRedPackService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 红包开通申请表 服务实现类
 * </p>
 *
 * @author qhliao
 * @since 2020-02-19
 */
@Service
public class SupportOpenRedPackServiceImpl extends ServiceImpl<SupportOpenRedPackMapper, SupportOpenRedPack> implements SupportOpenRedPackService {

    @Override
    public SupportOpenRedPack findByProjectCoin(String projectCoin) {
        return baseMapper.findByProjectCoin(projectCoin);
    }
}
