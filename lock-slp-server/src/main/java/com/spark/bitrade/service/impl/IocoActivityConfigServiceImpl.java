package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.mapper.IocoActivityConfigMapper;
import com.spark.bitrade.entity.IocoActivityConfig;
import com.spark.bitrade.service.IocoActivityConfigService;
import org.springframework.stereotype.Service;

/**
 * ioco活动配置(IocoActivityConfig)表服务实现类
 *
 * @author daring5920
 * @since 2019-07-03 14:38:58
 */
@Service("iocoActivityConfigService")
public class IocoActivityConfigServiceImpl extends ServiceImpl<IocoActivityConfigMapper, IocoActivityConfig> implements IocoActivityConfigService {

}