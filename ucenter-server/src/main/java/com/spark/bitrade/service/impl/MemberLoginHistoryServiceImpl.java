package com.spark.bitrade.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.spark.bitrade.constant.BooleanEnum;
import com.spark.bitrade.constant.LoginType;
import com.spark.bitrade.entity.Member;
import com.spark.bitrade.mapper.MemberLoginHistoryMapper;
import com.spark.bitrade.entity.MemberLoginHistory;
import com.spark.bitrade.service.MemberLoginHistoryService;
import com.spark.bitrade.util.EnumHelperUtil;
import com.spark.bitrade.util.IpUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;

/**
 * (MemberLoginHistory)表服务实现类
 *
 * @author wsy
 * @since 2019-06-14 14:55:06
 */
@Service("memberLoginHistoryService")
public class MemberLoginHistoryServiceImpl extends ServiceImpl<MemberLoginHistoryMapper, MemberLoginHistory> implements MemberLoginHistoryService {

    @Value("${login.check.area.ip:false}")
    private boolean checkLoginArea; // 登录时是否根据IP进行区域定位

    @Async
    @Override
    public void saveHistory(Member member, Map<String, String> property, LoginType type, String thirdMark, BooleanEnum isRegister) {
        MemberLoginHistory memberLoginHistory = new MemberLoginHistory();
        String isRootOrJailbreakStr = property.get("isRootOrJailbreak");
        memberLoginHistory.setProducers(property.get("producers"));
        memberLoginHistory.setSystemVersion(property.get("systemVersion"));
        memberLoginHistory.setModel(property.get("model"));
        memberLoginHistory.setUuid(property.get("uuid"));
        memberLoginHistory.setIsRegistrate(isRegister);
        //判断是否有手机root过的信息
        if (null != isRootOrJailbreakStr && !"".equals(isRootOrJailbreakStr)) {
            //如果有信息者将其装换为boolean枚举类型
            BooleanEnum isRootOrJailbreak = EnumHelperUtil.getByIntegerTypeCode(BooleanEnum.class, "getOrdinal", Integer.parseInt(isRootOrJailbreakStr));
            memberLoginHistory.setIsRootOrJailbreak(isRootOrJailbreak);
        }
        //设置其他信息
        memberLoginHistory.setMemberId(member.getId());
        // TODO 定位用户位置
        /*if(checkLoginArea) {
            DimArea dimArea = gyDmcodeService.getPostionInfo(null, null, IpUtils.getIpAddr(request));
            if (dimArea != null) {
                memberLoginHistory.setArea(dimArea.getAreaName());
            }
        }*/
        memberLoginHistory.setLoginip(property.get("loginIP"));
        memberLoginHistory.setThirdMark(thirdMark);
        memberLoginHistory.setType(type);
        memberLoginHistory.setLoginTime(new Date());
        save(memberLoginHistory);
    }
}