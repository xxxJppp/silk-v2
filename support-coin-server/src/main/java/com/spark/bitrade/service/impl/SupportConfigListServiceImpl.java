package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.entity.SupportConfigList;
import com.spark.bitrade.mapper.SupportConfigListMapper;
import com.spark.bitrade.service.SupportConfigListService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 扶持上币配置KEY-VALUE 服务实现类
 * </p>
 *
 * @author qiliao
 * @since 2019-11-04
 */
@Service
public class SupportConfigListServiceImpl extends ServiceImpl<SupportConfigListMapper, SupportConfigList> implements SupportConfigListService {


    public SupportConfigList findByKey(String configKey, String dicKey) {

        QueryWrapper<SupportConfigList> ac = new QueryWrapper<>();
        ac.lambda().eq(SupportConfigList::getConfigKey, configKey)
                .eq(SupportConfigList::getDictKey,dicKey);


        return this.getOne(ac);
    }


}
