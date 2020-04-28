package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.mapper.IocoMemberWalletMapper;
import com.spark.bitrade.entity.IocoMemberWallet;
import com.spark.bitrade.service.IocoMemberWalletService;
import org.springframework.stereotype.Service;

/**
 * ioco用户钱包数据(IocoMemberWallet)表服务实现类
 *
 * @author daring5920
 * @since 2019-07-03 14:38:58
 */
@Service("iocoMemberWalletService")
public class IocoMemberWalletServiceImpl extends ServiceImpl<IocoMemberWalletMapper, IocoMemberWallet> implements IocoMemberWalletService {

}