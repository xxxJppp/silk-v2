package com.spark.bitrade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.entity.MemberSecuritySet;

/**
 * 用户安全权限配置表服务接口
 *
 * @author wsy
 * @since 2019-06-14 14:20:15
 */
public interface MemberSecuritySetService extends IService<MemberSecuritySet> {

    MemberSecuritySet findByMemberId(Long memberId);
}