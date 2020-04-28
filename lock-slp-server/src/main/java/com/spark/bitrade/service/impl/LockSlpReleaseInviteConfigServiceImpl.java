package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.mapper.LockSlpReleaseInviteConfigMapper;
import com.spark.bitrade.entity.LockSlpReleaseInviteConfig;
import com.spark.bitrade.service.LockSlpReleaseInviteConfigService;
import org.springframework.stereotype.Service;

/**
 * 分享收益配置表(LockSlpReleaseInviteConfig)表服务实现类
 *
 * @author yangch
 * @since 2019-06-18 21:27:03
 */
@Service("lockSlpReleaseInviteConfigService")
public class LockSlpReleaseInviteConfigServiceImpl extends ServiceImpl<LockSlpReleaseInviteConfigMapper, LockSlpReleaseInviteConfig> implements LockSlpReleaseInviteConfigService {

}