package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.entity.*;
import com.spark.bitrade.mapper.UserConfigurationCenterMapper;
import com.spark.bitrade.service.UserConfigurationCenterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 个人中心-消息提醒设置
 *
 * @author zhongxj
 * @date 2019.10.21
 */
@Slf4j
@Service("userConfigurationCenterService")
public class UserConfigurationCenterServiceImpl extends ServiceImpl<UserConfigurationCenterMapper, UserConfigurationCenter> implements UserConfigurationCenterService {

    @Resource
    private UserConfigurationCenterMapper userConfigurationCenterMapper;

    /**
     * 新增
     *
     * @param memberId        会员ID
     * @param triggeringEvent 事件，触发事件{0:充值到账提醒,1:新订单创建提醒,2:交易即将过期,3:已付款提醒,4:已释放提醒,5:申诉处理结果提醒}
     * @param isSms           短信
     * @param isEmail         邮件
     * @param isApns          APP通知
     * @return
     */
    @Override
    public Integer addUserConfigurationCenter(Long memberId, Integer triggeringEvent, Integer isSms, Integer isEmail, Integer isApns) {
        return userConfigurationCenterMapper.addUserConfigurationCenter(memberId, triggeringEvent, isSms, isEmail, isApns);
    }

    /**
     * 修改
     *
     * @param memberId        会员ID
     * @param triggeringEvent 事件
     * @param channel         渠道
     * @param status          开关
     */
    @Override
    public Integer updateUserConfigurationCenter(Long memberId, Integer triggeringEvent, String channel, Integer status) {
        return userConfigurationCenterMapper.updateUserConfigurationCenter(memberId, triggeringEvent, channel, status);
    }
}