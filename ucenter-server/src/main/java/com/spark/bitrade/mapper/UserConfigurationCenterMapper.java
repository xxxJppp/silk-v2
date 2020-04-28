package com.spark.bitrade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spark.bitrade.entity.UserConfigurationCenter;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户配置中心mapper层
 *
 * @author zhongxj
 * @date 2019.09.11
 */
@Mapper
public interface UserConfigurationCenterMapper extends BaseMapper<UserConfigurationCenter> {

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
    Integer addUserConfigurationCenter(@Param("memberId") Long memberId, @Param("triggeringEvent") Integer triggeringEvent, @Param("isSms") Integer isSms, @Param("isEmail") Integer isEmail, @Param("isApns") Integer isApns);

    /**
     * 修改
     *
     * @param memberId        会员ID
     * @param triggeringEvent 事件
     * @param channel         渠道
     * @param status          开关
     */
    Integer updateUserConfigurationCenter(@Param("memberId") Long memberId, @Param("triggeringEvent") Integer triggeringEvent, @Param("channel") String channel, @Param("status") Integer status);

}
