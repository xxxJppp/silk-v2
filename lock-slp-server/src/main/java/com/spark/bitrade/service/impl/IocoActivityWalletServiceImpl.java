package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.mapper.IocoActivityWalletMapper;
import com.spark.bitrade.entity.IocoActivityWallet;
import com.spark.bitrade.service.IocoActivityWalletService;
import org.springframework.stereotype.Service;

/**
 * ioco活动钱包数据(IocoActivityWallet)表服务实现类
 *
 * @author daring5920
 * @since 2019-07-03 14:38:58
 */
@Service("iocoActivityWalletService")
public class IocoActivityWalletServiceImpl extends ServiceImpl<IocoActivityWalletMapper, IocoActivityWallet> implements IocoActivityWalletService {

}