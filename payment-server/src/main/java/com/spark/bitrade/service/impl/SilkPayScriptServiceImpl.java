package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.mapper.SilkPayScriptMapper;
import com.spark.bitrade.entity.SilkPayScript;
import com.spark.bitrade.service.SilkPayScriptService;
import org.springframework.stereotype.Service;

/**
 * 自动化脚本(SilkPayScript)表服务实现类
 *
 * @author wsy
 * @since 2019-07-18 10:39:15
 */
@Service("silkPayScriptService")
public class SilkPayScriptServiceImpl extends ServiceImpl<SilkPayScriptMapper, SilkPayScript> implements SilkPayScriptService {

}