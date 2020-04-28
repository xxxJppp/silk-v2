package com.spark.bitrade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.spark.bitrade.constant.BooleanEnum;
import com.spark.bitrade.constant.LoginType;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.entity.MemberLoginHistory;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * (MemberLoginHistory)表服务接口
 *
 * @author wsy
 * @since 2019-06-14 14:55:06
 */
public interface MemberLoginHistoryService extends IService<MemberLoginHistory> {

    void saveHistory(Member member, Map<String, String> property, LoginType type, String thirdMark, BooleanEnum isRegister);
}