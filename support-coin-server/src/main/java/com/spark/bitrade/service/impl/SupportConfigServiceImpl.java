package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.constant.ModuleType;
import com.spark.bitrade.constant.SupportCoinMsgCode;
import com.spark.bitrade.entity.SupportConfig;
import com.spark.bitrade.entity.SupportConfigList;
import com.spark.bitrade.exception.MessageCodeException;
import com.spark.bitrade.mapper.SupportConfigListMapper;
import com.spark.bitrade.mapper.SupportConfigMapper;
import com.spark.bitrade.service.SupportConfigService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 扶持上币配置 服务实现类
 * </p>
 *
 * @author qiliao
 * @since 2019-11-04
 */
@Service
public class SupportConfigServiceImpl extends ServiceImpl<SupportConfigMapper, SupportConfig> implements SupportConfigService {

    @Resource
    private SupportConfigListMapper supportConfigListMapper;

    @Override
    public List<SupportConfigList> findByModule(ModuleType moduleType) {
        QueryWrapper<SupportConfig> scq = new QueryWrapper<>();
        scq.lambda().eq(SupportConfig::getModuleType, moduleType)
                .eq(SupportConfig::getStatus, 1);
        List<SupportConfig> configs = this.list(scq);
        if (!CollectionUtils.isEmpty(configs)) {
            SupportConfig supportConfig = configs.get(0);
            QueryWrapper<SupportConfigList> sclq = new QueryWrapper<>();
            sclq.lambda().eq(SupportConfigList::getConfigKey, supportConfig.getConfigKey());
            List<SupportConfigList> configListList = supportConfigListMapper.selectList(sclq);
            return configListList;
        }
        return new ArrayList<>();
    }

    @Override
    public SupportConfig findConfigByModule(ModuleType moduleType) {
        QueryWrapper<SupportConfig> scq = new QueryWrapper<>();
        scq.lambda().eq(SupportConfig::getModuleType, moduleType)
                .eq(SupportConfig::getStatus, 1);
        List<SupportConfig> configs = this.list(scq);
        if (!CollectionUtils.isEmpty(configs)) {
            return configs.get(0);

        }
        throw new MessageCodeException(SupportCoinMsgCode.CONFIG_LIST_NOT_FIND);
    }
}
